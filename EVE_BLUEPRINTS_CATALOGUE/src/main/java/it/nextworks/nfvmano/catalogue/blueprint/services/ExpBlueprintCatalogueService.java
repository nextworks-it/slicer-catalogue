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
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.ExpBlueprintCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardExpBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryExpBlueprintResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.ExpBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.ExpBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.KeyPerformanceIndicatorRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;
import it.nextworks.nfvmano.libs.catalogues.interfaces.elements.AppPackageInfo;
import it.nextworks.nfvmano.libs.catalogues.interfaces.messages.*;
import it.nextworks.nfvmano.libs.common.exceptions.*;
import it.nextworks.nfvmano.libs.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.descriptors.nsd.Nsd;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpBlueprintCatalogueService implements ExpBlueprintCatalogueInterface {

	private static final Logger log = LoggerFactory.getLogger(ExpBlueprintCatalogueService.class);


	

	@Autowired
    private VsBlueprintCatalogueService vsBlueprintCatalogueService;
	
	@Autowired
	private CtxBlueprintRepository ctxBlueprintRepository;

	@Autowired
	private ExpBlueprintRepository expBlueprintRepository;

	@Autowired
	private ExpBlueprintInfoRepository expBlueprintInfoRepository;

	@Autowired
    private NfvoCatalogueService nfvoCatalogueService;

    @Autowired
    private TranslationRuleRepository translationRuleRepository;

    @Autowired
    private KeyPerformanceIndicatorRepository keyPerformanceIndicatorRepository;

	public ExpBlueprintCatalogueService() {	}
	
	@Override
	public synchronized String onboardExpBlueprint(OnboardExpBlueprintRequest request)
            throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException{
		log.debug("Processing request to onboard a new Exp blueprint");
		request.isValid();
        String expBinfoId = storeExpBlueprint(request.getExpBlueprint());
        String vsBlueprintId = request.getExpBlueprint().getVsBlueprintId();
        Optional<VsBlueprint> vsBlueprint= vsBlueprintCatalogueService.findByVsBlueprintId(vsBlueprintId);
        ExpBlueprintInfo expBlueprintInfo = expBlueprintInfoRepository.findByExpBlueprintId(expBinfoId).get();
        ExpBlueprint expBlueprint = expBlueprintRepository.findByExpBlueprintId(expBinfoId).get();
        if(request.getExpBlueprint().getKpis()!=null){

            for(KeyPerformanceIndicator kpi : request.getExpBlueprint().getKpis()){
                kpi.setBlueprint(expBlueprint);
                keyPerformanceIndicatorRepository.saveAndFlush(kpi);

            }
        }

        if(!vsBlueprint.isPresent())
            throw new FailedOperationException("VsBlueprint with ID:"+vsBlueprintId+" NOT FOUND");
        for(String ctxBlueprintId : request.getExpBlueprint().getCtxBlueprintIds()){
            Optional<CtxBlueprint> ctxBlueprint= ctxBlueprintRepository.findByBlueprintId(ctxBlueprintId);
            if(!ctxBlueprint.isPresent())
                throw new FailedOperationException("CtxBlueprint with ID:"+ctxBlueprintId+" NOT FOUND");

        }
        for(Nsd nsd : request.getNsds()){
            try {
                String nsdInfoId = nfvoCatalogueService.onboardNsd(new OnboardNsdRequest(nsd, null));
                log.debug("Added NSD " + nsd.getNsdIdentifier() +
                        ", version " + nsd.getVersion() + " in NFVO catalogue. NSD Info ID: " + nsdInfoId);
                expBlueprintInfo.addNsdInfoId(nsdInfoId);
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
                expBlueprintInfo.addNsdInfoId(oldNsdInfoId);
                request.setNsdInfoIdInTranslationRules(oldNsdInfoId, nsd.getNsdIdentifier(), nsd.getVersion());
            }
        }

        expBlueprintInfoRepository.saveAndFlush(expBlueprintInfo);
        log.debug("Storing translation rules");
        List<VsdNsdTranslationRule> trs = request.getExpTranslationRules();
        for (VsdNsdTranslationRule tr : trs) {
            translationRuleRepository.saveAndFlush(tr);
        }
        log.debug("Translation rules saved in internal DB.");
        //TODO: save translation rule in repo
        //expBlueprint.setTranslationRules(request.getExpTranslationRules());
        //expBlueprintRepository.saveAndFlush(expBlueprint);
        return expBinfoId;



	}



    @Override
	public QueryExpBlueprintResponse queryExpBlueprint(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		log.debug("Processing request to query a Exp blueprint");
		return  null;
	}
	
	@Override
	public synchronized void deleteExpBlueprint(String expBlueprintId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {

	}
	
	public synchronized void addExpdInBlueprint(String expBlueprintId, String expdId)
			throws NotExistingEntityException {
		log.debug("Adding EXPD " + expdId + " to blueprint " + expBlueprintId);

	}
	
	public synchronized void removeExpdInBlueprint(String expBlueprintId, String expdId)
			throws NotExistingEntityException {
		log.debug("Removing EXPD " + expdId + " from blueprint " + expBlueprintId);

	}
	

	private ExpBlueprint getExpBlueprint(String name, String version) throws NotExistingEntityException {
		return null;
	}
	
	private ExpBlueprintInfo getExpBlueprintInfo(String name, String version) throws NotExistingEntityException {

		return null;
	}
	
	private ExpBlueprint getExpBlueprint(String expbId) throws NotExistingEntityException {
		return null;
	}
	
	private ExpBlueprintInfo getExpBlueprintInfo(String expbId) throws NotExistingEntityException {

		return null;
	}
	
	private List<ExpBlueprintInfo> getAllExpBlueprintInfos() throws NotExistingEntityException {

		return null;
	}

	public Optional<ExpBlueprint> findByExpBlueprintId(String id){
		return expBlueprintRepository.findByExpBlueprintId(id);
	}


	public Optional<ExpBlueprint> findByNameAndVersion(String name, String version){
		return expBlueprintRepository.findByNameAndVersion(name,version);
	}


	private String storeExpBlueprint(ExpBlueprint expBlueprint) throws AlreadyExistingEntityException {

        log.debug("Onboarding EXP blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion());
        if ( (expBlueprintInfoRepository.findByNameAndExpBlueprintVersion(expBlueprint.getName(), expBlueprint.getVersion()).isPresent())) {
            log.error("EXP Blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion() + " already present in DB.");
            throw new AlreadyExistingEntityException("EXP Blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion() + " already present in DB.");
        }
        if ( (expBlueprintInfoRepository.findByExpBlueprintId(expBlueprint.getExpBlueprintId()).isPresent())) {
            log.error("EXP Blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion() + " already present in DB.");
            throw new AlreadyExistingEntityException("EXP Blueprint with id " + expBlueprint.getExpBlueprintId());
        }
        expBlueprintRepository.saveAndFlush(expBlueprint);
        ExpBlueprintInfo expbInfo = new ExpBlueprintInfo(expBlueprint.getExpBlueprintId(), expBlueprint.getVersion(), expBlueprint.getName());
        expBlueprintInfoRepository.saveAndFlush(expbInfo);


        return expbInfo.getExpBlueprintId();
    }


    private void processOnboardAppPackageRequests(List<OnboardAppPackageRequest> onboardAppPackageRequests) throws MalformattedElementException, NotExistingEntityException, MethodNotImplementedException, FailedOperationException {
        log.debug("Storing MEC app packages");

        for (OnboardAppPackageRequest appR : onboardAppPackageRequests) {
            try {
                OnboardAppPackageResponse appReply = nfvoCatalogueService.onboardAppPackage(appR);
                String appPackageId = appReply.getOnboardedAppPkgId();
                log.debug("Added MEC app package for app " + appR.getName() +
                        ", version " + appR.getVersion() + " in NFVO catalogue. MEC app package ID: " + appPackageId);

            } catch (AlreadyExistingEntityException e) {
                log.debug("The MEC app package is already present in the NFVO catalogue. Retrieving its ID.");
                List<AppPackageInfo> apps =
                        nfvoCatalogueService.queryApplicationPackage(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildMecAppPackageInfoFilter(appR.getName(), appR.getVersion()), null)).getQueryResult();
                String oldAppPackageId = apps.get(0).getAppPackageInfoId();
                log.debug("Retrieved MEC app package ID: " + oldAppPackageId);

            }
        }
    }
}
