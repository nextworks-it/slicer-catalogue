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

import it.nextworks.nfvmano.catalogue.blueprint.BlueprintCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.EveportalCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.elements.*;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.CtxBlueprintCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryCtxBlueprintResponse;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryCtxDescriptorResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsComponentRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsbForwardingPathHopRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsbLinkRepository;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.catalogues.interfaces.messages.OnboardNsdRequest;
import it.nextworks.nfvmano.libs.ifa.catalogues.interfaces.messages.QueryNsdResponse;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CtxBlueprintCatalogueService implements CtxBlueprintCatalogueInterface {

	private static final Logger log = LoggerFactory.getLogger(CtxBlueprintCatalogueService.class);

	@Autowired
	private CtxBlueprintRepository ctxBlueprintRepository;
	
	@Autowired
	private TranslationRuleRepository translationRuleRepository;
	
	@Autowired
	private VsbForwardingPathHopRepository vsbForwardingPathHopRepository;
	
	@Autowired
	private VsbLinkRepository vsbLinkRepository;

	@Autowired
	private CtxBlueprintInfoRepository ctxBlueprintInfoRepository;
	
	@Autowired
	private CtxDescriptorCatalogueService ctxDescriptorCatalogueService;

	@Autowired
	private VsComponentRepository vsComponentRepository;

	@Autowired
	private NfvoCatalogueService nfvoCatalogueService;

	public CtxBlueprintCatalogueService() {	}
	
	@Override
	public synchronized String onboardCtxBlueprint(OnboardCtxBlueprintRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
		log.debug("Process CtxBlueprint Onboard request");
		request.isValid();

		Map<String, Nsd> nsdInfoIdNsd = storeNsds(request);
		//The nsInfoId doesnot seem to be used afterwards, candidate for removal.
		//Comment the following line to disable the changes
		nsdInfoIdNsd = new HashMap<>();

		CtxBlueprint ctxBlueprint = request.getCtxBlueprint();
		String ctxBlueprintId = storeCtxBlueprint(ctxBlueprint, request.getOwner(), nsdInfoIdNsd);
		CtxBlueprintInfo ctxBlueprintInfo;
		try {
			ctxBlueprintInfo = getContextBlueprintInfo(ctxBlueprintId);
		} catch (NotExistingEntityException e) {
			log.error("Impossible to retrieve contextBlueprintInfo. Error!");
			throw new FailedOperationException("Internal error: impossible to retrieve contextBlueprintInfo.");
		}
		storeTranslationRules(request, ctxBlueprintInfo, new HashMap<>());
		return ctxBlueprintId;
	}
	
	@Override
	public QueryCtxBlueprintResponse queryCtxBlueprint(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		log.debug("Processing request to query a Context blueprint");
		request.isValid();
		
		//At the moment the only filters accepted are:
		//1. Context Blueprint name and version
		//CTXB_NAME & CTXB_VERSION
		//2. Context Blueprint ID
		//CTXB_ID
		//3. Tenant ID
		//TENANT_ID
		//4. Context Blueprint ID and Tenant ID
		//CTXB_ID && TENANT_ID 
        //No attribute selector is supported at the moment
		
		List<CtxBlueprintInfo> ctxbs = new ArrayList<>();
		
		Filter filter = request.getFilter();
        List<String> attributeSelector = request.getAttributeSelector();
        if ((attributeSelector == null) || (attributeSelector.isEmpty())) {
        	Map<String, String> fp = filter.getParameters();
            if (fp.size() == 1 && fp.containsKey("CTXB_ID")) {
            	String ctxbId = fp.get("CTXB_ID");
            	CtxBlueprintInfo ctxb = getContextBlueprintInfo(ctxbId);
            	ctxbs.add(ctxb);
            	log.debug("Added Context Blueprint info for CTXB ID " + ctxbId);
            } else if (fp.size() == 2 && fp.containsKey("CTXB_NAME") && fp.containsKey("CTXB_VERSION")) {
            	String ctxbName = fp.get("CTXB_NAME");
            	String ctxbVersion = fp.get("CTXB_VERSION");
            	CtxBlueprintInfo ctxb = getContextBlueprintInfo(ctxbName, ctxbVersion);
            	ctxbs.add(ctxb);
            	log.debug("Added Context Blueprint info for CTXB with name " + ctxbName + " and version " + ctxbVersion);
            } else if (fp.size() == 2 && fp.containsKey("CTXB_ID") && fp.containsKey("TENANT_ID")) {
            	String ctxbId = fp.get("CTXB_ID");
            	String tenantId = fp.get("TENANT_ID");
            	CtxBlueprintInfo origCtxb = getContextBlueprintInfo(ctxbId);
            	CtxBlueprintInfo ctxb = postProcessCtxb(origCtxb, tenantId);
            	ctxbs.add(ctxb);
            	log.debug("Added Context Blueprint info for CTXB ID " + ctxbId + " with ctxD filtered for tenant " + tenantId);
            } else if (fp.size() == 1 && fp.containsKey("TENANT_ID")) {
            	String tenantId = fp.get("TENANT_ID");
            	List<CtxBlueprintInfo> origCtxbs = getAllContextBlueprintInfos();
            	for (CtxBlueprintInfo x : origCtxbs) {
            		CtxBlueprintInfo ctxb = postProcessCtxb(x, tenantId);
            		ctxbs.add(ctxb);
            	}
            	log.debug("Added all the VSB info available in DB filtering VSDs for tenant " + tenantId);
            } else if (fp.isEmpty()) {
            	ctxbs = getAllContextBlueprintInfos();
            	log.debug("Addes all the VSB info available in DB.");
            }
            return new QueryCtxBlueprintResponse(ctxbs);
        } else {
            log.error("Received query Context Blueprint with attribute selector. Not supported at the moment.");
            throw new MethodNotImplementedException("Received query Context Blueprint with attribute selector. Not supported at the moment.");
        }
		
	}
	
	private CtxBlueprintInfo postProcessCtxb(CtxBlueprintInfo origCtxb, String tenantId) {
		List<String> origCtxdIds = origCtxb.getActiveCtxdId();
		CtxBlueprintInfo targetCtxb = origCtxb;
		targetCtxb.removeAllCtxd();
		for (String s : origCtxdIds) {
			try {
				QueryCtxDescriptorResponse rp = ctxDescriptorCatalogueService.queryCtxDescriptor(
						new GeneralizedQueryRequest(
								EveportalCatalogueUtilities.buildCtxDescriptorFilter(s, tenantId),
								null
						)
				);
				if (rp != null) {
					log.debug("CTX descriptor with ID " + s + " found for tenant " + tenantId + ". Adding CTXD ID into CTXB info.");
					targetCtxb.addCtxd(s);
				}
			} catch (Exception e) {
				log.debug("CTX Descriptor with ID " + s + " not found for tenant " + tenantId);
			}
		}
		return targetCtxb;
	}
	
	@Override
	public synchronized void deleteCtxBlueprint(String ctxBlueprintId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		
		log.debug("Processing request to delete a context blueprint with ID " + ctxBlueprintId);
		
		if (ctxBlueprintId == null) throw new MalformattedElementException("The context blueprint ID is null");
		
		CtxBlueprintInfo ctxbi = getContextBlueprintInfo(ctxBlueprintId);
		
		if (!(ctxbi.getActiveCtxdId().isEmpty())) {
			log.error("There are some context descriptors associated to the Context Blueprint. Impossible to remove it.");
			throw new FailedOperationException("There are some context descriptors associated to the Context Blueprint. Impossible to remove it.");
		}
		
		ctxBlueprintInfoRepository.delete(ctxbi);
		log.debug("Removed context blueprint info from DB.");
		CtxBlueprint ctxb = getContextBlueprint(ctxBlueprintId);
		ctxBlueprintRepository.delete(ctxb);
		log.debug("Removed context blueprint from DB.");
		
	}

	public synchronized void addCtxdInBlueprint(String ctxBlueprintId, String cxtDescriptorId) 
			throws NotExistingEntityException {
		log.debug("Adding Context Descriptor " + cxtDescriptorId + " to Context Blueprint " + ctxBlueprintId);
		CtxBlueprintInfo ctxbi = getContextBlueprintInfo(ctxBlueprintId);
		ctxbi.addCtxd(cxtDescriptorId);
		ctxBlueprintInfoRepository.saveAndFlush(ctxbi);
		log.debug("Added Context Descriptor " + cxtDescriptorId + " to Context Blueprint " + ctxBlueprintId);
	}
	
	public synchronized void removeCtxdInBlueprint(String ctxBlueprintId, String cxtDescriptorId) 
			throws NotExistingEntityException {
		log.debug("Removing Context Descriptor " + cxtDescriptorId + " from Context Blueprint " + ctxBlueprintId);
		CtxBlueprintInfo ctxbi = getContextBlueprintInfo(ctxBlueprintId);
		ctxbi.removeCtxd(cxtDescriptorId);
		ctxBlueprintInfoRepository.saveAndFlush(ctxbi);
		log.debug("Removed Context Descriptor " + cxtDescriptorId + " from Context Blueprint " + ctxBlueprintId);
	}

	/**
	 * @return  A Map with the nsInfoId as the key and the Nsd as value

	 */
	private Map<String, Nsd> storeNsds(OnboardCtxBlueprintRequest request) throws FailedOperationException, MethodNotImplementedException, MalformattedElementException {

		Map<String, Nsd> nsdInfoIdToNsd = new HashMap<>();
		log.debug("Storing NSDs");
		for(Nsd nsd : request.getNsds()){
			String nsdInfoId = null;
			String nsdId = nsd.getNsdIdentifier();
			try {
				Map<String, String> userDefinedData = new HashMap<>();
				List<EveSite> sites = request.getCtxBlueprint().getCompatibleSites();
				for (EveSite site : sites) {
					userDefinedData.put(site.toString(), "yes");
				}

				//TODO: Verify this, the number returned is randomly generated
				nsdInfoId = nfvoCatalogueService.onboardNsd(new OnboardNsdRequest(nsd, userDefinedData));
				log.debug("Added NSD " + nsd.getNsdIdentifier() +
						", version " + nsd.getVersion() + " in NFVO catalogue. NSD Info ID: " + nsdInfoId);
				log.debug("NsdId to NsdInfoId Mapping:"+nsdId+" "+nsdInfoId);
				nsdInfoIdToNsd.put(nsdInfoId, nsd);
			} catch (AlreadyExistingEntityException e) {
				log.debug("The NSD is already present in the NFVO catalogue. IGNORING");

				/*QueryNsdResponse nsdR = null;
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
		return  nsdInfoIdToNsd;


	}

	private String storeCtxBlueprint(CtxBlueprint ctxBlueprint, String owner, Map<String, Nsd> nsdIdToInfoId) throws AlreadyExistingEntityException {
        log.debug("Onboarding CTX blueprint with name " + ctxBlueprint.getName() + " and version " + ctxBlueprint.getVersion());
        if ( (ctxBlueprintInfoRepository.findByNameAndCtxBlueprintVersion(ctxBlueprint.getName(), ctxBlueprint.getVersion()).isPresent()) ||
                (ctxBlueprintRepository.findByNameAndVersion(ctxBlueprint.getName(), ctxBlueprint.getVersion()).isPresent()) ) {
            log.error("CTX Blueprint with name " + ctxBlueprint.getName() + " and version " + ctxBlueprint.getVersion() + " already present in DB.");
            throw new AlreadyExistingEntityException("CTX Blueprint with name " + ctxBlueprint.getName() + " and version " + ctxBlueprint.getVersion() + " already present in DB.");
        }

        CtxBlueprint target = new CtxBlueprint(null,
        		ctxBlueprint.getVersion(),
                ctxBlueprint.getName(),
                ctxBlueprint.getDescription(),
                ctxBlueprint.getParameters(),
                ctxBlueprint.getEndPoints(),
                ctxBlueprint.getConfigurableParameters(),
                ctxBlueprint.getCompatibleSites(),
                ctxBlueprint.getApplicationMetrics(),
				ctxBlueprint.getCompositionStrategy());


        ctxBlueprintRepository.saveAndFlush(target);
        Long ctxbId = target.getId();
        String ctxbIdString = String.valueOf(ctxbId);
        target.setBlueprintId(ctxbIdString);
        ctxBlueprintRepository.saveAndFlush(target);
        log.debug("Added CTX Blueprint Info with ID " + ctxbIdString);
        
        List<VsComponent> atomicComponents = ctxBlueprint.getAtomicComponents();
		if (atomicComponents != null) {
			for (VsComponent c : atomicComponents) {
				VsComponent targetComponent = new VsComponent(target, c.getComponentId(), c.getServersNumber(), c.getImagesUrls(), c.getEndPointsIds(), c.getLifecycleOperations(),
						c.getNfvId(), c.getPlacement());
				vsComponentRepository.saveAndFlush(targetComponent);
			}
			log.debug("Added atomic components in VS blueprint " + ctxbIdString);
		}
		
		List<VsbForwardingPathHop> hops = ctxBlueprint.getServiceSequence();
		if (hops != null) {
			for (VsbForwardingPathHop hop : hops) {
				VsbForwardingPathHop targetHop = new VsbForwardingPathHop(target, hop.getHopEndPoints());
				vsbForwardingPathHopRepository.saveAndFlush(targetHop);
			}
			log.debug("Added VSB FP hop in VS blueprint " + ctxbIdString);
		}
		
		List<VsbLink> connectivityServices = ctxBlueprint.getConnectivityServices();
		if (connectivityServices != null) {
			for (VsbLink l : connectivityServices) {
				VsbLink targetLink = new VsbLink(target, l.getEndPointIds(), l.isExternal(), l.getConnectivityProperties(), l.getName(), l.isManagement());
				vsbLinkRepository.saveAndFlush(targetLink);
			}
			log.debug("Added connectivity services in VS blueprint " + ctxbIdString);
		}
		
        CtxBlueprintInfo ctxBlueprintInfo = new CtxBlueprintInfo(ctxbIdString, ctxBlueprint.getVersion(), ctxBlueprint.getName(), owner);
        ctxBlueprintInfoRepository.saveAndFlush(ctxBlueprintInfo);
        log.debug("Added Context Blueprint Info with ID " + ctxbIdString);

        log.debug("Adding NsdInfoIds in ctxbluepritinfo");
        for(String nsdInfoId: nsdIdToInfoId.keySet()){
        	ctxBlueprintInfo.addNsdInfoId(nsdInfoId);
		}
        ctxBlueprintInfoRepository.saveAndFlush(ctxBlueprintInfo);
        return ctxbIdString;
	}

	private CtxBlueprint getContextBlueprint(String blueprintId) throws NotExistingEntityException {
		Optional<CtxBlueprint> ctxBlueprintOpt = ctxBlueprintRepository.findByBlueprintId(blueprintId);
		if (ctxBlueprintOpt.isPresent()) return ctxBlueprintOpt.get();
		else throw new NotExistingEntityException("Context Blueprint with ID " + blueprintId + " not found in DB.");
	}
	
	private CtxBlueprint getContextBlueprint(String name, String version) throws NotExistingEntityException {
		Optional<CtxBlueprint> ctxBlueprintOpt = ctxBlueprintRepository.findByNameAndVersion(name, version);
		if (ctxBlueprintOpt.isPresent()) return ctxBlueprintOpt.get();
		else throw new NotExistingEntityException("Context Blueprint with name " + name + " and version " + version + " not found in DB.");
	}
	
	private CtxBlueprintInfo getContextBlueprintInfo(String blueprintId) throws NotExistingEntityException {
		CtxBlueprintInfo ctxBlueprintInfo;
		Optional<CtxBlueprintInfo> ctxBlueprintInfoOpt = ctxBlueprintInfoRepository.findByCtxBlueprintId(blueprintId);
		if (ctxBlueprintInfoOpt.isPresent()) ctxBlueprintInfo = ctxBlueprintInfoOpt.get();
		else throw new NotExistingEntityException("Context Blueprint info for context blueprint with ID " + blueprintId + " not found in DB.");
		CtxBlueprint ctxb = getContextBlueprint(blueprintId);
		ctxBlueprintInfo.setCtxBlueprint(ctxb);
		return ctxBlueprintInfo;
	}
	
	private CtxBlueprintInfo getContextBlueprintInfo(String name, String version) throws NotExistingEntityException {
		CtxBlueprintInfo ctxBlueprintInfo;
		Optional<CtxBlueprintInfo> ctxBlueprintInfoOpt = ctxBlueprintInfoRepository.findByNameAndCtxBlueprintVersion(name, version);
		if (ctxBlueprintInfoOpt.isPresent()) ctxBlueprintInfo = ctxBlueprintInfoOpt.get();
		else throw new NotExistingEntityException("Context Blueprint info for CTXB with name " + name + " and version " + version + " not found in DB.");
		CtxBlueprint ctxb = getContextBlueprint(name, version);
		ctxBlueprintInfo.setCtxBlueprint(ctxb);
		return ctxBlueprintInfo;
	}

	private List<CtxBlueprintInfo> getAllContextBlueprintInfos() throws NotExistingEntityException {
		List<CtxBlueprintInfo> ctxbs = ctxBlueprintInfoRepository.findAll();

		for (CtxBlueprintInfo ctxbi : ctxbs) {
			String name = ctxbi.getName();
			String version = ctxbi.getCtxBlueprintVersion();
			CtxBlueprint ctxb = getContextBlueprint(name, version);
			ctxbi.setCtxBlueprint(ctxb);
		}
		return ctxbs;
	}


	private void storeTranslationRules(OnboardCtxBlueprintRequest request, CtxBlueprintInfo ctxBlueprintInfo, Map<String, Nsd> nsInfoIdNsd){

		log.debug("Storing translation rules");
		String ctxBlueprintId = ctxBlueprintInfo.getCtxBlueprintId();
		request.setBlueprintIdInTranslationRules(ctxBlueprintId);

		if(nsInfoIdNsd!=null && !nsInfoIdNsd.isEmpty()){
			log.debug("Adding nsdInfoId in translation rules");
			for(Map.Entry<String, Nsd> entry:  nsInfoIdNsd.entrySet()){
				request.setNsdInfoIdInTranslationRules(entry.getKey(), entry.getValue().getNsdIdentifier(), entry.getValue().getVersion());
			}
		}

		List<VsdNsdTranslationRule> trs = request.getTranslationRules();
		for (VsdNsdTranslationRule tr : trs) {
			translationRuleRepository.saveAndFlush(tr);
		}
		log.debug("Translation rules saved in internal DB.");
		ctxBlueprintInfoRepository.saveAndFlush(ctxBlueprintInfo);
	}
}
