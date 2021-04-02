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

import java.util.*;

import it.nextworks.nfvmano.catalogue.blueprint.BlueprintCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.elements.*;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.VsBlueprintCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsBlueprintRepository;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import it.nextworks.nfvmano.libs.ifa.catalogues.interfaces.messages.OnboardNsdRequest;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;
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
	
	@Autowired
	private NfvoCatalogueService nfvoCatalogueService;
	
	@Autowired
	private VsDescriptorCatalogueService vsDescriptorCatalogueService;

	@Autowired
	private AuthService authService;
	
	public VsBlueprintCatalogueService() {	}
	
	@Override
	public synchronized String onBoardVsBlueprint(OnBoardVsBlueprintRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
		log.debug("Processing request to onboard a new VS blueprint");
		request.isValid();
		if(request.getVsBlueprint().getCompatibleSites()==null || request.getVsBlueprint().getCompatibleSites().isEmpty()){
			throw new MalformattedElementException("Vertical service blueprint without compatible sites");
		}
		if(request.getVsBlueprint().getApplicationMetrics()!=null){
			for(ApplicationMetric am: request.getVsBlueprint().getApplicationMetrics()){
				am.isValid();
			}
		}
		if (request.getVsBlueprint().isInterSite()){
			for(VsComponent component : request.getVsBlueprint().getAtomicComponents()){
				if(component.getType()!=null && component.getType().equals(VsComponentType.SERVICE)){
					Optional<VsBlueprint> nestedVsb = vsBlueprintRepository.findByBlueprintId(component.getAssociatedVsbId());
					if(!nestedVsb.isPresent())
						throw  new MalformattedElementException("Could not find associated VSB with ID:"+component.getAssociatedVsbId());

					List<VsBlueprintParameter> nestedParameters = request.getVsBlueprint().getParameters();
					for(VsBlueprintParameter nestedParameter: nestedVsb.get().getParameters()){
						String nestedParameterId = component.getComponentId()+"."+nestedParameter.getParameterId();
						if(!isParameterInList(nestedParameterId, nestedParameters)){
							log.debug("nested parameter not included, adding it:"+nestedParameterId);
							nestedParameters.add(new VsBlueprintParameter(nestedParameterId, nestedParameter.getParameterName(), nestedParameter.getParameterType(),
									nestedParameter.getParameterDescription(), nestedParameter.getApplicabilityField()));
						}
					}

				}

			}

		}
		Map<String, Nsd> nsdInfoNsd = storeNsds(request);
		//disabled nsdInfoId setting in blueprint/translationrule
		nsdInfoNsd = new HashMap<>();
		String vsbId = storeVsBlueprint(request, request.getOwner(), nsdInfoNsd);
		
		VsBlueprintInfo vsBlueprintInfo;
		try {
			vsBlueprintInfo = getVsBlueprintInfo(vsbId);
		} catch (NotExistingEntityException e) {
			log.error("Impossible to retrieve vsBlueprintInfo. Error!");
			throw new FailedOperationException("Internal error: impossible to retrieve vsBlueprintInfo.");
		}
		
		storeTranslationRules(request, vsBlueprintInfo, nsdInfoNsd);
		return vsbId;
			

	}


	private boolean isParameterInList(String parameterId, List<VsBlueprintParameter> parameterList){
		Optional<VsBlueprintParameter> parameter = parameterList.stream()
														.filter(p -> p.getParameterId().equals(parameterId))
														.findFirst();
		return parameter.isPresent();
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
            } else if (fp.size() == 1 && fp.containsKey("SITE")) {
            	String siteStr = fp.get("SITE");
            	EveSite site = EveSite.valueOf(siteStr);
            	List<VsBlueprintInfo> vsb = getVsBlueprintInfoFromSite(site);
            	vsbs.addAll(vsb);
            	log.debug("Added VSBs info");
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
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String tenantId = authService.getUserFromAuth(authentication);
		log.debug("Processing request to delete a VS blueprint with ID " + vsBlueprintId);
		boolean catalogueAdmin = authService.isCatalogueAdminUser(authentication);
		if (vsBlueprintId == null) throw new MalformattedElementException("The VS blueprint ID is null");


		VsBlueprintInfo vsbi = getVsBlueprintInfo(vsBlueprintId);

		if(catalogueAdmin || vsbi.getOwner().equals(tenantId)){
			if (!(vsbi.getActiveVsdId().isEmpty())) {
				log.error("There are some VSDs associated to the VS Blueprint. Impossible to remove it.");
				throw new FailedOperationException("There are some VSDs associated to the VS Blueprint. Impossible to remove it.");
			}

			vsBlueprintInfoRepository.delete(vsbi);
			log.debug("Removed VSB info from DB.");
			VsBlueprint vsb = getVsBlueprint(vsBlueprintId);
			vsBlueprintRepository.delete(vsb);
			log.debug("Removed VSB from DB.");
		}else throw new FailedOperationException("Logged user cannot delete the specified VSB:"+tenantId+" "+vsBlueprintId);

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
	
	private String storeVsBlueprint(OnBoardVsBlueprintRequest request, String owner, Map<String, Nsd> nsdInfoNsd) throws AlreadyExistingEntityException, MalformattedElementException {
		VsBlueprint vsBlueprint = request.getVsBlueprint();
		log.debug("Onboarding VS blueprint with name " + vsBlueprint.getName() + " and version " + vsBlueprint.getVersion());
		if ( (vsBlueprintInfoRepository.findByNameAndVsBlueprintVersion(vsBlueprint.getName(), vsBlueprint.getVersion()).isPresent()) ||
				(vsBlueprintRepository.findByNameAndVersion(vsBlueprint.getName(), vsBlueprint.getVersion()).isPresent()) ) {
			log.error("VS Blueprint with name " + vsBlueprint.getName() + " and version " + vsBlueprint.getVersion() + " already present in DB.");
			throw new AlreadyExistingEntityException("VS Blueprint with name " + vsBlueprint.getName() + " and version " + vsBlueprint.getVersion() + " already present in DB.");
		}

		if (request.getVsBlueprint().isInterSite()){
			for(VsComponent component : request.getVsBlueprint().getAtomicComponents()){
				if(component.getType().equals(VsComponentType.SERVICE)){
					Optional<VsBlueprint> nestedVsb = vsBlueprintRepository.findByBlueprintId(component.getAssociatedVsbId());
					if(!nestedVsb.isPresent())
						throw  new MalformattedElementException("Could not find associated VSB with ID:"+component.getAssociatedVsbId());

					List<VsBlueprintParameter> nestedParameters = request.getVsBlueprint().getParameters();
					for(VsBlueprintParameter nestedParameter: nestedVsb.get().getParameters()){
						String nestedParameterId = component.getComponentId()+"."+nestedParameter.getParameterId();
						if(!isParameterInList(nestedParameterId, nestedParameters)){
							log.debug("nested parameter not included, adding it:"+nestedParameterId);
							nestedParameters.add(new VsBlueprintParameter(nestedParameterId, nestedParameter.getParameterName(), nestedParameter.getParameterType(),
									nestedParameter.getParameterDescription(), nestedParameter.getApplicabilityField()));
						}
					}

				}

			}

		}
		VsBlueprint target = new VsBlueprint(null, vsBlueprint.getVersion(), vsBlueprint.getName(), vsBlueprint.getDescription(), vsBlueprint.getParameters(),
				vsBlueprint.getEndPoints(), vsBlueprint.getConfigurableParameters(), 
				vsBlueprint.getCompatibleSites(), vsBlueprint.getCompatibleContextBlueprint(),
				vsBlueprint.getApplicationMetrics(), vsBlueprint.isInterSite());
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
						c.getNfvId(), c.getPlacement(),
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
				VsbLink targetLink = new VsbLink(target, l.getEndPointIds(), l.isExternal(), l.getConnectivityProperties(), l.getName(), l.isManagement());
				vsbLinkRepository.saveAndFlush(targetLink);
			}
			log.debug("Added connectivity services in VS blueprint " + vsbIdString);
		}
		
		VsBlueprintInfo vsBlueprintInfo = new VsBlueprintInfo(vsbIdString, vsBlueprint.getVersion(), vsBlueprint.getName(), owner);
		vsBlueprintInfoRepository.saveAndFlush(vsBlueprintInfo);
		log.debug("Added VS Blueprint Info with ID " + vsbIdString);

		if(nsdInfoNsd!=null && !nsdInfoNsd.isEmpty()) {
			log.debug("Adding  VS Blueprint Info nsdInfoIds");
			for (String nsdInfoId : nsdInfoNsd.keySet()) {
				vsBlueprintInfo.addNsdInfoId(nsdInfoId);
			}
			vsBlueprintInfoRepository.saveAndFlush(vsBlueprintInfo);
		}


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
	}


	private Map<String, Nsd> storeNsds(OnBoardVsBlueprintRequest request) throws FailedOperationException, MalformattedElementException, MethodNotImplementedException {

		log.debug("Storing NSDs");
		List<Nsd> nsds = request.getNsds();
		Map<String, Nsd> nsdInfoIdNsd = new HashMap<>();
		for (Nsd nsd : nsds) {
			String nsdInfoId = null;
			try {
				Map<String, String> userDefinedData = new HashMap<>();
				if(!request.getVsBlueprint().isInterSite()){
					log.debug("onboarding single site NSDs for VSB");

					List<EveSite> sites = request.getVsBlueprint().getCompatibleSites();
					for (EveSite site : sites) {
						userDefinedData.put(site.toString(), "yes");
						if(site==EveSite.FRANCE_RENNES || site==EveSite.FRANCE_NICE|| site==EveSite.FRANCE_PARIS||site==EveSite.FRANCE_SACLAY
								||site==EveSite.FRANCE_CHATILLON||site==EveSite.FRANCE_SOPHIA_ANTIPOLIS||site==EveSite.FRANCE_LANNION){
							log.debug("Adding nsd_invariant_id to userDefinedData:"+nsd.getNsdInvariantId());
							userDefinedData.put("NSD_INVARIANT_ID",nsd.getNsdInvariantId());

						}
					}

				}else{
					log.debug("onboarding inter-site NSDs for VSB");
					if(nsd.getNestedNsdId()==null || nsd.getNestedNsdId().isEmpty())
						throw new MalformattedElementException("Inter-site VSB with non composite NSD");
					userDefinedData.put("multiSite","yes");


				}
				nsdInfoId = nfvoCatalogueService.onboardNsd(new OnboardNsdRequest(nsd, userDefinedData));
				log.debug("Added NSD " + nsd.getNsdIdentifier() +
						", version " + nsd.getVersion() + " in NFVO catalogue. NSD Info ID: " + nsdInfoId);

				nsdInfoIdNsd.put(nsdInfoId, nsd);

			} catch (AlreadyExistingEntityException e) {
				log.debug("The NSD is already present in the NFVO catalogue. IGNORING.");

				/*
				QueryNsdResponse nsdR = null;
				try {
					nsdR = nfvoCatalogueService.queryNsd(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildNsdInfoFilter(nsd.getNsdIdentifier(), nsd.getVersion()), null));
				} catch (NotExistingEntityException ex) {
					throw new FailedOperationException("Something went wrong interacting with the NFVO Catalogue: "+ex.getMessage());
				}
				nsdInfoId = nsdR.getQueryResult().get(0).getNsdInfoId();
				log.debug("Retrieved NSD Info ID: " + nsdInfoId);
				*/

			}

		}
		return nsdInfoIdNsd;
	}

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


	private void storeTranslationRules (OnBoardVsBlueprintRequest request, VsBlueprintInfo vsBlueprintInfo, Map<String,Nsd> nsdInfoIdNsd ){

		log.debug("Storing translation rules");
		String vsBlueprintId = vsBlueprintInfo.getVsBlueprintId();
		request.setBlueprintIdInTranslationRules(vsBlueprintId);

		if(nsdInfoIdNsd!=null && !nsdInfoIdNsd.isEmpty()){
			log.debug("Adding nsdInfoId in translation rules");
			for(Map.Entry<String, Nsd> entry:  nsdInfoIdNsd.entrySet()){
				request.setNsdInfoIdInTranslationRules(entry.getKey(), entry.getValue().getNsdIdentifier(), entry.getValue().getVersion());
			}
		}

		List<VsdNsdTranslationRule> trs = request.getTranslationRules();
		for (VsdNsdTranslationRule tr : trs) {
			translationRuleRepository.saveAndFlush(tr);
		}
		log.debug("Translation rules saved in internal DB.");
		vsBlueprintInfoRepository.saveAndFlush(vsBlueprintInfo);
	}
}
