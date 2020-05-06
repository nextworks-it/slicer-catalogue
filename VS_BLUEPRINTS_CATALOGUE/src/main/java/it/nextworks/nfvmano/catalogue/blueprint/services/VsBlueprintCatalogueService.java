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

import it.nextworks.nfvmano.catalogue.blueprint.elements.*;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.VsBlueprintCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnBoardVsBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryVsBlueprintResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.*;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
	private NstTranslationRuleRepository nstTranslationRuleRepository;
	
	@Autowired
	private NfvoCatalogueService nfvoCatalogueService;
	
	public VsBlueprintCatalogueService() {	}
	
	@Override
	public synchronized String onBoardVsBlueprint(OnBoardVsBlueprintRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
		log.debug("Processing request to on board a new VS blueprint");
		request.isValid();
		String vsbId = storeVsBlueprint(request.getVsBlueprint());
		
		VsBlueprintInfo vsBlueprintInfo;
		try {
			vsBlueprintInfo = getVsBlueprintInfo(vsbId);
		} catch (NotExistingEntityException e) {
			log.error("Impossible to retrieve vsBlueprintInfo. Error!");
			throw new FailedOperationException("Internal error: impossible to retrieve vsBlueprintInfo.");
		}
		
		request.setBlueprintIdInTranslationRules(vsbId);

		try {
			vsBlueprintInfoRepository.saveAndFlush(vsBlueprintInfo);
			
			log.debug("Storing translation rules");
			List<VsdNstTranslationRule> trs = request.getNstTranslationRules();
			for (VsdNstTranslationRule tr : trs) {
				nstTranslationRuleRepository.saveAndFlush(tr);
			}
			log.debug("Translation rules saved in internal DB.");
			
			return vsbId;
			
		} catch (Exception e) {
			log.error("Something went wrong when processing SO descriptors.");
			throw new FailedOperationException("Internal error: something went wrong when processing SO descriptors.");
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
		
		vsBlueprintInfoRepository.delete(vsbi.getId());
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
	
	private String storeVsBlueprint(VsBlueprint vsBlueprint) throws AlreadyExistingEntityException {
		log.debug("Onboarding VS blueprint with name " + vsBlueprint.getName() + " and version " + vsBlueprint.getVersion());
		if ( (vsBlueprintInfoRepository.findByNameAndVsBlueprintVersion(vsBlueprint.getName(), vsBlueprint.getVersion()).isPresent()) ||
				(vsBlueprintRepository.findByNameAndVersion(vsBlueprint.getName(), vsBlueprint.getVersion()).isPresent()) ) {
			log.error("VS Blueprint with name " + vsBlueprint.getName() + " and version " + vsBlueprint.getVersion() + " already present in DB.");
			throw new AlreadyExistingEntityException("VS Blueprint with name " + vsBlueprint.getName() + " and version " + vsBlueprint.getVersion() + " already present in DB.");
		}
		
		VsBlueprint target = new VsBlueprint(null, vsBlueprint.getVersion(), vsBlueprint.getName(), vsBlueprint.getDescription(), vsBlueprint.getParameters(),
				vsBlueprint.getEndPoints(), vsBlueprint.getConfigurableParameters(), 
				vsBlueprint.getCompatibleSites(), vsBlueprint.getCompatibleContextBlueprint(),
				vsBlueprint.getApplicationMetrics());
		target.setPpFunctionList(vsBlueprint.getPpFunctionList());
		vsBlueprintRepository.saveAndFlush(target);
		
		Long vsbId = target.getId();
		String vsbIdString = String.valueOf(vsbId);
		target.setBlueprintId(vsbIdString);
		vsBlueprintRepository.saveAndFlush(target);
		log.debug("Added VS Blueprint with ID " + vsbIdString);
		
		List<VsComponent> atomicComponents = vsBlueprint.getAtomicComponents();
		if (atomicComponents != null) {
			for (VsComponent c : atomicComponents) {
				VsComponent targetComponent = new VsComponent(target, c.getComponentId(), c.getServersNumber(), c.getImagesUrls(), c.getEndPointsIds(), c.getLifecycleOperations());
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
				VsbLink targetLink = new VsbLink(target, l.getEndPointIds(), l.isExternal(), l.getConnectivityProperties());
				vsbLinkRepository.saveAndFlush(targetLink);
			}
			log.debug("Added connectivity services in VS blueprint " + vsbIdString);
		}
		
		VsBlueprintInfo vsBlueprintInfo = new VsBlueprintInfo(vsbIdString, vsBlueprint.getVersion(), vsBlueprint.getName());
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
	
}
