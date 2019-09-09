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
import it.nextworks.nfvmano.catalogue.blueprint.elements.*;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.CtxBlueprintCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryCtxBlueprintResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsComponentRepository;
import it.nextworks.nfvmano.libs.catalogues.interfaces.messages.OnboardNsdRequest;
import it.nextworks.nfvmano.libs.catalogues.interfaces.messages.QueryNsdResponse;
import it.nextworks.nfvmano.libs.common.exceptions.*;
import it.nextworks.nfvmano.libs.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.descriptors.nsd.Nsd;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CtxBlueprintCatalogueService implements CtxBlueprintCatalogueInterface {

	private static final Logger log = LoggerFactory.getLogger(CtxBlueprintCatalogueService.class);

	@Autowired
	private CtxBlueprintRepository ctxBlueprintRepository;


	@Autowired
	private TranslationRuleRepository translationRuleRepository;

	@Autowired
	private CtxBlueprintInfoRepository ctxBlueprintInfoRepository;

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
		CtxBlueprint ctxBlueprint = request.getCtxBlueprint();
		String ctxBlueprintInfoId = storeCtxBlueprint(ctxBlueprint);
		CtxBlueprintInfo ctxBlueprintInfo = ctxBlueprintInfoRepository.findByCtxBlueprintId(ctxBlueprintInfoId).get();

		for(Nsd nsd : request.getNsds()){
			try {
				String nsdInfoId = nfvoCatalogueService.onboardNsd(new OnboardNsdRequest(nsd, null));
				log.debug("Added NSD " + nsd.getNsdIdentifier() +
						", version " + nsd.getVersion() + " in NFVO catalogue. NSD Info ID: " + nsdInfoId);
				ctxBlueprintInfo.addNsdInfoId(nsdInfoId);
				request.setNsdInfoIdInTranslationRules(nsdInfoId, nsd.getNsdIdentifier(), nsd.getVersion());
			} catch (AlreadyExistingEntityException e) {
				log.debug("The NSD is already present in the NFVO catalogue. Retrieving its ID.");
				QueryNsdResponse nsdR = null;
				try {
					nsdR = nfvoCatalogueService.queryNsd(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildNsdInfoFilter(nsd.getNsdIdentifier(), nsd.getVersion()), null));
				} catch (NotExistingEntityException ex) {
					throw new FailedOperationException("Something went wrong interacting with the NFVO Catalogue: "+ex.getMessage());
				}
				String oldNsdInfoId = nsdR.getQueryResult().get(0).getNsdInfoId();
				log.debug("Retrieved NSD Info ID: " + oldNsdInfoId);
				ctxBlueprintInfo.addNsdInfoId(oldNsdInfoId);
				request.setNsdInfoIdInTranslationRules(oldNsdInfoId, nsd.getNsdIdentifier(), nsd.getVersion());
			}
		}
		log.debug("Storing translation rules");
		List<VsdNsdTranslationRule> trs = this.saveTranslationRules(request.getTranslationRules());
		log.debug("Translation rules saved in internal DB.");

		for (VsComponent comp: ctxBlueprint.getAtomicComponents()){
			vsComponentRepository.saveAndFlush(comp);
		}
		ctxBlueprint.setCtxTranslationRules(trs);
		ctxBlueprintRepository.saveAndFlush(ctxBlueprint);
		return ctxBlueprintInfoId;
	}
	
	@Override
	public QueryCtxBlueprintResponse queryCtxBlueprint(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		//TODO
		return null;
	}
	
	@Override
	public synchronized void deleteCtxBlueprint(String ctxBlueprintId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		//TODO
	}

	@Override
	public Optional<CtxBlueprint> findByCtxBlueprintId(String ctxBlueprintId) {
		//TODO
		return Optional.empty();
	}

	@Override
	public Optional<CtxBlueprint> findByNameAndVersion(String name, String version) {
		//TODO
		return Optional.empty();
	}

	public synchronized void addCtxdInBlueprint(String vsBlueprintId, String vsdId)
			throws NotExistingEntityException {
		//TODO
	}







	private String storeCtxBlueprint(CtxBlueprint ctxBlueprint) throws AlreadyExistingEntityException {
        log.debug("Onboarding CTX blueprint with name " + ctxBlueprint.getName() + " and version " + ctxBlueprint.getVersion());
        if ( (ctxBlueprintInfoRepository.findByNameAndCtxBlueprintVersion(ctxBlueprint.getName(), ctxBlueprint.getVersion()).isPresent()) ||
                (ctxBlueprintRepository.findByNameAndVersion(ctxBlueprint.getName(), ctxBlueprint.getVersion()).isPresent()) ) {
            log.error("CTX Blueprint with name " + ctxBlueprint.getName() + " and version " + ctxBlueprint.getVersion() + " already present in DB.");
            throw new AlreadyExistingEntityException("CTX Blueprint with name " + ctxBlueprint.getName() + " and version " + ctxBlueprint.getVersion() + " already present in DB.");
        }


        List<VsComponent> targetComponents = this.saveAtomicComponents(ctxBlueprint.getAtomicComponents());

		List<VsdNsdTranslationRule> targetRules = saveTranslationRules(ctxBlueprint.getCtxTranslationRules() );
        CtxBlueprint target = new CtxBlueprint(
                null,
                ctxBlueprint.getVersion(),
                ctxBlueprint.getName(),
                ctxBlueprint.getDescription(),
                ctxBlueprint.getParameters(),
                targetComponents,
                ctxBlueprint.getEndPoints(),
                //ctxBlueprint.getConstraints(),
                ctxBlueprint.getMetrics(),
                ctxBlueprint.getConfigurableParameters(),
                ctxBlueprint.getServiceSequence(),
				targetRules);

        ctxBlueprintRepository.saveAndFlush(target);
        Long ctxbId = target.getId();
        String ctxbIdString = String.valueOf(ctxbId);
        target.setCtxBlueprintId(ctxbIdString);
        log.debug("Added CTX Blueprint Info with ID " + ctxbIdString);

        CtxBlueprintInfo ctxBlueprintInfo = new CtxBlueprintInfo(ctxbIdString, ctxBlueprint.getVersion(), ctxBlueprint.getName());
        ctxBlueprintInfoRepository.saveAndFlush(ctxBlueprintInfo);

        return ctxbIdString;
	}

	private List<VsComponent> saveAtomicComponents( List<VsComponent> atomicComponents){

		List<VsComponent> targetComponents = new ArrayList<>();
		if (atomicComponents != null) {
			for (VsComponent c : atomicComponents) {
				//first element is null because it should be a VSB
				VsComponent targetComponent = new VsComponent(null, c.getComponentId(), c.getServersNumber(), c.getImagesUrls(), c.getEndPointsIds(), c.getLifecycleOperations());
				vsComponentRepository.saveAndFlush(targetComponent);
				targetComponents.add(targetComponent);
			}
			//log.debug("Added atomic components in CTX blueprint " + ctxbIdString);
		}

		return targetComponents;
	}

	private List<VsdNsdTranslationRule> saveTranslationRules( List<VsdNsdTranslationRule> translationRules){

		List<VsdNsdTranslationRule> targetRules = new ArrayList<>();
		if(translationRules!=null){
			for(VsdNsdTranslationRule rule: translationRules){
				VsdNsdTranslationRule newRule = new VsdNsdTranslationRule(
						rule.getInput(),
						rule.getNsdId(),
						rule.getNsdVersion(),
						rule.getNsFlavourId(),
						rule.getNsInstantiationLevelId());

				translationRuleRepository.saveAndFlush(newRule);
				targetRules.add(newRule);




			}
		}
		return targetRules;
	}



}
