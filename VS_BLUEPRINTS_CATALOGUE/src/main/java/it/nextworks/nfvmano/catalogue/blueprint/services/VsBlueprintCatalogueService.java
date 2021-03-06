/*
 * Copyright 2018 Nextworks s.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.nextworks.nfvmano.catalogue.blueprint.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.nextworks.nfvmano.catalogue.blueprint.BlueprintCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.VsBlueprintCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsBlueprintRepository;
import it.nextworks.nfvmano.catalogue.template.interfaces.NsTemplateCatalogueInterface;
import it.nextworks.nfvmano.catalogue.template.messages.OnBoardNsTemplateRequest;
import it.nextworks.nfvmano.catalogue.template.messages.QueryNsTemplateResponse;
import it.nextworks.nfvmano.catalogues.template.services.NsTemplateCatalogueService;
import it.nextworks.nfvmano.libs.ifa.catalogues.interfaces.messages.*;
import it.nextworks.nfvmano.libs.ifa.templates.NST;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;

import it.nextworks.nfvmano.catalogue.blueprint.elements.VsBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsBlueprintInfo;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsComponent;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsbLink;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsbForwardingPathHop;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnBoardVsBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryVsBlueprintResponse;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryVsDescriptorResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsComponentRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsbForwardingPathHopRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsbLinkRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;



@Service
public class VsBlueprintCatalogueService implements VsBlueprintCatalogueInterface {

	private static final Logger log = LoggerFactory.getLogger(VsBlueprintCatalogueService.class);
	
	@Autowired
	private VsBlueprintRepository vsBlueprintRepository;
	
	@Autowired
	private VsBlueprintInfoRepository vsBlueprintInfoRepository;
	
	@Autowired
	private VsbForwardingPathHopRepository vsbForwardingPathHopRepository;
	
	@Autowired
	private VsbLinkRepository vsbLinkRepository;
	
	@Autowired
	private VsComponentRepository vsComponentRepository;
	
	@Autowired
	private TranslationRuleRepository translationRuleRepository;


	private NsTemplateCatalogueInterface nsTemplateCatalogueService;


	@Autowired
	private VsDescriptorCatalogueService vsDescriptorCatalogueService;
	
	public VsBlueprintCatalogueService() {	}
	
	@Override
	public synchronized String onBoardVsBlueprint(OnBoardVsBlueprintRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
		log.debug("Processing request to onboard a new VS blueprint");
		request.isValid();


		this.processNsDescriptorOnboarding(request);

		String vsbId = storeVsBlueprint(request.getVsBlueprint(), request.getOwner());

		VsBlueprintInfo vsBlueprintInfo;
		try {
			vsBlueprintInfo = getVsBlueprintInfo(vsbId);
		} catch (NotExistingEntityException e) {
			log.error("Impossible to retrieve vsBlueprintInfo. Error!");
			throw new FailedOperationException("Internal error: impossible to retrieve vsBlueprintInfo.");
		}

		request.setBlueprintIdInTranslationRules(vsbId);
		vsBlueprintInfoRepository.saveAndFlush(vsBlueprintInfo);
		log.debug("Storing translation rules");
		List<VsdNsdTranslationRule> trs = request.getTranslationRules();
		for (VsdNsdTranslationRule tr : trs) {
			translationRuleRepository.saveAndFlush(tr);
		}
		log.debug("Translation rules saved in internal DB.");

		return vsbId;


	}

	private void processNsDescriptorOnboarding(OnBoardVsBlueprintRequest request) throws MalformattedElementException, FailedOperationException, AlreadyExistingEntityException, MethodNotImplementedException {

		if(request.getNsts()!=null && request.getNsts().isEmpty() && request.getNsds()!=null && request.getNsds().isEmpty()
		&& request.getVnfPackages()!=null && request.getVnfPackages().isEmpty()
		&& request.getPnfds()!=null && request.getPnfds().isEmpty()){
			log.debug("No descriptors to onboard, moving on");
			return;
		}

		if(nsTemplateCatalogueService==null && request.getNsts()!=null && !request.getNsts().isEmpty()) {
			throw new MalformattedElementException("Onboarding request including NS descriptors, but no NSTemplateCatalogueProvider set");

		}
		
		if(request.getNsts()!=null && !request.getNsts().isEmpty()){
			OnBoardNsTemplateRequest nstRequest = new OnBoardNsTemplateRequest(request.getNsts().get(0), request.getNsds(), request.getVnfPackages(),request.getPnfds(),request.getConfigurationRules());
			nsTemplateCatalogueService.onBoardNsTemplate(nstRequest);
			for(int i=1; i<request.getNsts().size(); i++) {
				nstRequest = new OnBoardNsTemplateRequest(request.getNsts().get(i), null, null,null,null);
				nsTemplateCatalogueService.onBoardNsTemplate(nstRequest);
			}
		}else{
			log.debug("No NST to be onboarded, skipping NSD/NST onboard");
		}




	}

	@Override
	public QueryVsBlueprintResponse queryVsBlueprint(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		log.debug("Processing request to query a VS blueprint");
		request.isValid();

		//At the moment the only filters accepted are:
		//1. VS Blueprint name and version
		//VSB_NAME & VSB_VERSION
		//2. VS Blueprint ID
		//VSB_ID
		//3. Site
		//SITE
		//4. VS Blueprint ID and tenant ID
		//VSB_ID and TENANT_ID --> This modifies the list of associated VSD to visualize only the ones visible to that VSD
		//5. Tenant ID
		//TENANT_ID --> This modifies the list of associated VSD to visualize only the ones visible to that VSD
		//No attribute selector is supported at the moment

		List<VsBlueprintInfo> vsbs = new ArrayList<>();

		Filter filter = request.getFilter();
		List<String> attributeSelector = request.getAttributeSelector();
		if ((attributeSelector == null) || (attributeSelector.isEmpty())) {
			Map<String, String> fp = filter.getParameters();
			if (fp.size() == 1 && fp.containsKey("VSB_ID")) {
				String vsbId = fp.get("VSB_ID");
				VsBlueprintInfo vsb = getVsBlueprintInfo(vsbId);
				vsbs.add(vsb);
				log.debug("Added VSB info for VSB ID " + vsbId);

            } else if (fp.size() == 2 && fp.containsKey("VSB_NAME") && fp.containsKey("VSB_VERSION")) {
            	String vsbName = fp.get("VSB_NAME");
            	String vsbVersion = fp.get("VSB_VERSION");
            	VsBlueprintInfo vsb = getVsBlueprintInfo(vsbName, vsbVersion);
            	vsbs.add(vsb);
            	log.debug("Added VSB info for VSB with name " + vsbName + " and version " + vsbVersion);
            } else if (fp.size() == 2 && fp.containsKey("VSB_ID") && fp.containsKey("TENANT_ID")) {
            	String vsbId = fp.get("VSB_ID");
            	String tenantId = fp.get("TENANT_ID");
            	VsBlueprintInfo origVsb = getVsBlueprintInfo(vsbId);
            	VsBlueprintInfo vsb = postProcessVsb(origVsb, tenantId);
            	vsbs.add(vsb);
            	log.debug("Added VSB info for VSB with ID " + vsbId + " filtering VSDs for tenant " + tenantId);
            } else if (fp.size() == 1 && fp.containsKey("TENANT_ID")) {
            	String tenantId = fp.get("TENANT_ID");
            	List<VsBlueprintInfo> origVsbs = getAllVsBlueprintInfos();
            	for (VsBlueprintInfo x : origVsbs) {
            		VsBlueprintInfo vsb = postProcessVsb(x, tenantId);
            		vsbs.add(vsb);
            	}
            	log.debug("Added all the VSB info available in DB filtering VSDs for tenant " + tenantId);
            } else if (fp.isEmpty()) {
            	vsbs = getAllVsBlueprintInfos();
            	log.debug("Addes all the VSB info available in DB.");
            }
            return new QueryVsBlueprintResponse(vsbs);
        } else {
            log.error("Received query VS Bluepring with attribute selector. Not supported at the moment.");
            throw new MethodNotImplementedException("Received query VS Blueprint with attribute selector. Not supported at the moment.");
        }
	}
	
	private VsBlueprintInfo postProcessVsb(VsBlueprintInfo origVsb, String tenantId) {
		List<String> origVsdIds = origVsb.getActiveVsdId();
		VsBlueprintInfo targetVsb = origVsb;
		targetVsb.removeAllVsds();
		for (String s : origVsdIds) {
			try {
				QueryVsDescriptorResponse rp = vsDescriptorCatalogueService.queryVsDescriptor(
						new GeneralizedQueryRequest(
								BlueprintCatalogueUtilities.buildVsDescriptorFilter(s, tenantId),
								null
						)
				);
				if (rp != null) {
					log.debug("VS descriptor with ID " + s + " found for tenant " + tenantId + ". Adding VSD ID into VSB info.");
					targetVsb.addVsd(s);
				}
			} catch (Exception e) {
				log.debug("Descriptor with ID " + s + " not found for tenant " + tenantId);
			}
		}
		return targetVsb;
	}
	
	@Override
	public synchronized void deleteVsBlueprint(String vsBlueprintId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		log.debug("Processing request to delete a VS blueprint with ID " + vsBlueprintId);
		
		if (vsBlueprintId == null) throw new MalformattedElementException("The VS blueprint ID is null");
		
		VsBlueprintInfo vsbi = getVsBlueprintInfo(vsBlueprintId);
		
		if (!(vsbi.getActiveVsdId().isEmpty())) {
			log.error("There are some VSDs associated to the VS Blueprint. Impossible to remove it.");
			throw new FailedOperationException("There are some VSDs associated to the VS Blueprint. Impossible to remove it.");
		}
		
		vsBlueprintInfoRepository.delete(vsbi);
		log.debug("Removed VSB info from DB.");
		VsBlueprint vsb = getVsBlueprint(vsBlueprintId);
		vsBlueprintRepository.delete(vsb);
		log.debug("Removed VSB from DB.");
	}
	
	public synchronized void addVsdInBlueprint(String vsBlueprintId, String vsdId) 
			throws NotExistingEntityException {
		log.debug("Adding VSD " + vsdId + " to blueprint " + vsBlueprintId);
		VsBlueprintInfo vsbi = getVsBlueprintInfo(vsBlueprintId);
		vsbi.addVsd(vsdId);
		vsBlueprintInfoRepository.saveAndFlush(vsbi);
		log.debug("Added VSD " + vsdId + " to blueprint " + vsBlueprintId);
	}
	
	public synchronized void removeVsdInBlueprint(String vsBlueprintId, String vsdId) 
			throws NotExistingEntityException {
		log.debug("Removing VSD " + vsdId + " from blueprint " + vsBlueprintId);
		VsBlueprintInfo vsbi = getVsBlueprintInfo(vsBlueprintId);
		vsbi.removeVsd(vsdId);
		vsBlueprintInfoRepository.saveAndFlush(vsbi);
		log.debug("Removed VSD " + vsdId + " from blueprint " + vsBlueprintId);
	}
	
	private String storeVsBlueprint(VsBlueprint vsBlueprint, String owner) throws AlreadyExistingEntityException {
		log.debug("Onboarding VS blueprint with name " + vsBlueprint.getName() + " and version " + vsBlueprint.getVersion());
		if ( (vsBlueprintInfoRepository.findByNameAndVsBlueprintVersion(vsBlueprint.getName(), vsBlueprint.getVersion()).isPresent()) ||
				(vsBlueprintRepository.findByNameAndVersion(vsBlueprint.getName(), vsBlueprint.getVersion()).isPresent()) ) {
			log.error("VS Blueprint with name " + vsBlueprint.getName() + " and version " + vsBlueprint.getVersion() + " already present in DB.");
			throw new AlreadyExistingEntityException("VS Blueprint with name " + vsBlueprint.getName() + " and version " + vsBlueprint.getVersion() + " already present in DB.");
		}
		
		VsBlueprint target = new VsBlueprint(null, vsBlueprint.getVersion(), vsBlueprint.getName(), vsBlueprint.getDescription(), vsBlueprint.getParameters(),
				vsBlueprint.getEndPoints(), vsBlueprint.getConfigurableParameters(), 
				//vsBlueprint.getCompatibleSites(),
				//vsBlueprint.getCompatibleContextBlueprint(),
				vsBlueprint.getApplicationMetrics(),
				vsBlueprint.getSliceServiceType(),
				vsBlueprint.getEmbbServiceCategory(),
				vsBlueprint.getUrllcServiceCategory(),
				vsBlueprint.isInterSite());
		vsBlueprintRepository.saveAndFlush(target);

		Long vsbId = target.getId();
		String vsbIdString = String.valueOf(vsbId);
		target.setBlueprintId(vsbIdString);
		vsBlueprintRepository.saveAndFlush(target);
		log.debug("Added VS Blueprint with ID " + vsbIdString);
		
		List<VsComponent> atomicComponents = vsBlueprint.getAtomicComponents();
		if (atomicComponents != null) {
			for (VsComponent c : atomicComponents) {
				VsComponent targetComponent = new VsComponent(target,
						c.getComponentId(),
						c.getServersNumber(),
						c.getImagesUrls(),
						c.getEndPointsIds(),
						c.getLifecycleOperations(),
						c.getType(),
						c.getPlacement(),
						c.getAssociatedVsbId(),
						c.getCompatibleSite());
				vsComponentRepository.saveAndFlush(targetComponent);
			}
			log.debug("Added atomic components in VS blueprint " + vsbIdString);
		}
		
		List<VsbForwardingPathHop> hops = vsBlueprint.getServiceSequence();
		if (hops != null) {
			for (VsbForwardingPathHop hop : hops) {
				VsbForwardingPathHop targetHop = new VsbForwardingPathHop(target, hop.getHopEndPoints());
				vsbForwardingPathHopRepository.saveAndFlush(targetHop);
			}
			log.debug("Added VSB FP hop in VS blueprint " + vsbIdString);
		}
		
		List<VsbLink> connectivityServices = vsBlueprint.getConnectivityServices();
		if (connectivityServices != null) {
			for (VsbLink l : connectivityServices) {
				VsbLink targetLink = new VsbLink(target, l.getEndPointIds(), l.isExternal(),l.getName(), l.getConnectivityProperties());
				vsbLinkRepository.saveAndFlush(targetLink);
			}
			log.debug("Added connectivity services in VS blueprint " + vsbIdString);
		}
		
		VsBlueprintInfo vsBlueprintInfo = new VsBlueprintInfo(vsbIdString, vsBlueprint.getVersion(), vsBlueprint.getName(), owner);
		vsBlueprintInfoRepository.saveAndFlush(vsBlueprintInfo);
		log.debug("Added VS Blueprint Info with ID " + vsbIdString);
		
		return vsbIdString;
	}

	private VsBlueprint getVsBlueprint(String name, String version) throws NotExistingEntityException {
		if (vsBlueprintRepository.findByNameAndVersion(name, version).isPresent()) return vsBlueprintRepository.findByNameAndVersion(name, version).get();
		else throw new NotExistingEntityException("VS Blueprint with name " + name + " and version " + version + " not found in DB.");
	}
	
	private VsBlueprintInfo getVsBlueprintInfo(String name, String version) throws NotExistingEntityException {
		VsBlueprintInfo vsBlueprintInfo;
		if (vsBlueprintInfoRepository.findByNameAndVsBlueprintVersion(name, version).isPresent()) vsBlueprintInfo = vsBlueprintInfoRepository.findByNameAndVsBlueprintVersion(name, version).get();
		else throw new NotExistingEntityException("VS Blueprint info for VSB with name " + name + " and version " + version + " not found in DB.");
		VsBlueprint vsb = getVsBlueprint(name, version);
		vsBlueprintInfo.setVsBlueprint(vsb);
		return vsBlueprintInfo;
	}
	
	private VsBlueprint getVsBlueprint(String vsbId) throws NotExistingEntityException {
		if (vsBlueprintRepository.findByBlueprintId(vsbId).isPresent()) return vsBlueprintRepository.findByBlueprintId(vsbId).get();
		else throw new NotExistingEntityException("VS Blueprint with ID " + vsbId + " not found in DB.");
	}
	
	private VsBlueprintInfo getVsBlueprintInfo(String vsbId) throws NotExistingEntityException {
		VsBlueprintInfo vsBlueprintInfo;
		if (vsBlueprintInfoRepository.findByVsBlueprintId(vsbId).isPresent()) vsBlueprintInfo = vsBlueprintInfoRepository.findByVsBlueprintId(vsbId).get();
		else throw new NotExistingEntityException("VS Blueprint info for VSB with ID " + vsbId + " not found in DB.");
		VsBlueprint vsb = getVsBlueprint(vsbId);
		vsBlueprintInfo.setVsBlueprint(vsb);
		return vsBlueprintInfo;
	}
	
	/*
	private List<VsBlueprintInfo> getVsBlueprintInfoFromSite(EveSite site) throws NotExistingEntityException {
		log.debug("Searching for VSB compatible with site " + site);
		List<VsBlueprint> vsbs = vsBlueprintRepository.findByCompatibleSitesIn(site);
		if (vsbs.isEmpty()) throw new NotExistingEntityException("VS blueprint for site " + site + " not found in DB");
		List<VsBlueprintInfo> vsbis = new ArrayList<>();
		for (VsBlueprint vsb : vsbs) {
			String vsbId = vsb.getBlueprintId();
			VsBlueprintInfo vsbi = vsBlueprintInfoRepository.findByVsBlueprintId(vsbId).get();
			vsbi.setVsBlueprint(vsb);
			vsbis.add(vsbi);
			log.debug("Added VSB " + vsbId);
		}
		return vsbis;
	}*/
	
	private List<VsBlueprintInfo> getAllVsBlueprintInfos() throws NotExistingEntityException {
		List<VsBlueprintInfo> vsbis = vsBlueprintInfoRepository.findAll();
		for (VsBlueprintInfo vsbi : vsbis) {
			String name = vsbi.getName();
			String version = vsbi.getVsBlueprintVersion();
			VsBlueprint vsb = getVsBlueprint(name, version);
			vsbi.setVsBlueprint(vsb);
		}
		return vsbis;
	}


	public void setNsTemplateCatalogueService(NsTemplateCatalogueInterface nsTemplateCatalogueService){
		log.debug("Setting NsTemplateCatalogueService");
		this.nsTemplateCatalogueService= nsTemplateCatalogueService;
	}

}
