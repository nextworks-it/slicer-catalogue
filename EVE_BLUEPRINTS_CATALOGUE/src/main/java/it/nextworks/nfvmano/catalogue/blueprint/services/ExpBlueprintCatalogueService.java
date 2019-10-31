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
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardExpBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryExpBlueprintResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.ExpBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.ExpBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.KeyPerformanceIndicatorRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TestCaseBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TestCaseBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsBlueprintRepository;
import it.nextworks.nfvmano.libs.ifa.catalogues.interfaces.messages.*;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExpBlueprintCatalogueService implements ExpBlueprintCatalogueInterface {

	private static final Logger log = LoggerFactory.getLogger(ExpBlueprintCatalogueService.class);
	
	@Autowired
	private CtxBlueprintRepository ctxBlueprintRepository;
	
	@Autowired
	private CtxBlueprintInfoRepository ctxBlueprintInfoRepository;

	@Autowired
	private ExpBlueprintRepository expBlueprintRepository;

	@Autowired
	private ExpBlueprintInfoRepository expBlueprintInfoRepository;
	
	@Autowired
	private TestCaseBlueprintRepository testCaseBlueprintRepository;
	
	@Autowired
	private TestCaseBlueprintInfoRepository testCaseBlueprintInfoRepository;
	
	@Autowired
    private NfvoCatalogueService nfvoCatalogueService;

    @Autowired
    private TranslationRuleRepository translationRuleRepository;
    
    @Autowired
	private VsBlueprintRepository vsBlueprintRepository;
	
	@Autowired
	private VsBlueprintInfoRepository vsBlueprintInfoRepository;

    @Autowired
    private KeyPerformanceIndicatorRepository keyPerformanceIndicatorRepository;

	public ExpBlueprintCatalogueService() {	}
	
	@Override
	public synchronized String onboardExpBlueprint(OnboardExpBlueprintRequest request)
            throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException, NotExistingEntityException {
		log.debug("Processing request to onboard a new Exp blueprint");
		request.isValid();
		ExpBlueprint expB = request.getExpBlueprint();
        verifyExperimentBlueprintDependencies(expB);
        String experimentId = storeExpBlueprint(expB);
        
        ExpBlueprintInfo expBlueprintInfo;
		try {
			expBlueprintInfo = getExpBlueprintInfo(experimentId);
		} catch (NotExistingEntityException e) {
			log.error("Impossible to retrieve expBlueprintInfo. Error!");
			throw new FailedOperationException("Internal error: impossible to retrieve expBlueprintInfo.");
		}
		
		request.setBlueprintIdInTranslationRules(experimentId);
		
		log.debug("Processing NFV descriptors");
		try {
			log.debug("Storing NSDs");
			List<Nsd> nsds = request.getNsds();
			for (Nsd nsd : nsds) {
				try {
					String nsdInfoId = nfvoCatalogueService.onboardNsd(new OnboardNsdRequest(nsd, null));
					log.debug("Added NSD " + nsd.getNsdIdentifier() + 
							", version " + nsd.getVersion() + " in NFVO catalogue. NSD Info ID: " + nsdInfoId);
					expBlueprintInfo.addNsdInfoId(nsdInfoId);
					request.setNsdInfoIdInTranslationRules(nsdInfoId, nsd.getNsdIdentifier(), nsd.getVersion());
				} catch (AlreadyExistingEntityException e) {
					log.debug("The NSD is already present in the NFVO catalogue. Retrieving its ID.");
					QueryNsdResponse nsdR = nfvoCatalogueService.queryNsd(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildNsdInfoFilter(nsd.getNsdIdentifier(), nsd.getVersion()), null));
					String oldNsdInfoId = nsdR.getQueryResult().get(0).getNsdInfoId();
					log.debug("Retrieved NSD Info ID: " + oldNsdInfoId);
					expBlueprintInfo.addNsdInfoId(oldNsdInfoId);
					request.setNsdInfoIdInTranslationRules(oldNsdInfoId, nsd.getNsdIdentifier(), nsd.getVersion());
				}
			}
			expBlueprintInfoRepository.saveAndFlush(expBlueprintInfo);
			
			log.debug("Storing translation rules");
			List<VsdNsdTranslationRule> trs = request.getTranslationRules();
			for (VsdNsdTranslationRule tr : trs) {
				translationRuleRepository.saveAndFlush(tr);
			}
			log.debug("Translation rules saved in internal DB.");
			
			return experimentId;
			
		} catch (Exception e) {
			log.error("Something went wrong when processing NFV descriptors.");
			throw new FailedOperationException("Internal error: something went wrong when processing NFV descriptors.");
		}
		
	}



    @Override
	public QueryExpBlueprintResponse queryExpBlueprint(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		log.debug("Processing request to query an Experiment blueprint");
		request.isValid();
		
		//At the moment the only filters accepted are:
		//1. Exp Blueprint name and version
		//EXPB_NAME & EXPB_VERSION
		//2. EXPB Blueprint ID
		//EXPB_ID
		//3. ID of the VS Blueprint associated to the expeirment blueprint
		//VSB_ID
        //No attribute selector is supported at the moment
		
		List<ExpBlueprintInfo> expbs = new ArrayList<>();
		
		Filter filter = request.getFilter();
        List<String> attributeSelector = request.getAttributeSelector();
        if ((attributeSelector == null) || (attributeSelector.isEmpty())) {
        	Map<String, String> fp = filter.getParameters();
            if (fp.size() == 1 && fp.containsKey("EXPB_ID")) {
            	String expbId = fp.get("EXPB_ID");
            	ExpBlueprintInfo expb = getExpBlueprintInfo(expbId);
            	expbs.add(expb);
            	log.debug("Added EXPB info for EXPB ID " + expbId);
            } else if (fp.size() == 1 && fp.containsKey("VSB_ID")) {
            	String vsbId = fp.get("VSB_ID");
            	List<ExpBlueprintInfo> expb = getExpBlueprintInfoFromVsb(vsbId);
            	expbs.addAll(expb);
            	log.debug("Added EXPB info for all EXPBs associated to VSB ID " + vsbId);
            } else if (fp.size() == 2 && fp.containsKey("EXPB_NAME") && fp.containsKey("EXPB_VERSION")) {
            	String expbName = fp.get("EXPB_NAME");
            	String expbVersion = fp.get("EXPB_VERSION");
            	ExpBlueprintInfo expb = getExpBlueprintInfo(expbName, expbVersion);
            	expbs.add(expb);
            	log.debug("Added EXPB info for EXPB with name " + expbName + " and version " + expbVersion);
            } else if (fp.isEmpty()) {
            	expbs = getAllExpBlueprintInfos();
            	log.debug("Addes all the ExpB info available in DB.");
            }
            return new QueryExpBlueprintResponse(expbs);
        } else {
            log.error("Received query Experiment Bluepring with attribute selector. Not supported at the moment.");
            throw new MethodNotImplementedException("Received query Experiment Blueprint with attribute selector. Not supported at the moment.");
        }
	}
	
	@Override
	public synchronized void deleteExpBlueprint(String expBlueprintId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		log.debug("Processing request to delete a Experiment blueprint with ID " + expBlueprintId);
		
		if (expBlueprintId == null) throw new MalformattedElementException("The Experiment blueprint ID is null");
		
		ExpBlueprintInfo expbi = getExpBlueprintInfo(expBlueprintId);
		
		if (!(expbi.getActiveExpdId().isEmpty())) {
			log.error("There are some ExpDs associated to the Experiment Blueprint. Impossible to remove it.");
			throw new FailedOperationException("There are some ExpDs associated to the Experiment Blueprint. Impossible to remove it.");
		}
		
		expBlueprintInfoRepository.delete(expbi);
		log.debug("Removed ExpB info from DB.");
		ExpBlueprint expb = getExpBlueprint(expBlueprintId);
		expBlueprintRepository.delete(expb);
		log.debug("Removed ExpB from DB.");
	}
	
	public synchronized void addExpdInBlueprint(String expBlueprintId, String expdId)
			throws NotExistingEntityException {
		log.debug("Adding EXPD " + expdId + " to blueprint " + expBlueprintId);
		ExpBlueprintInfo expi = getExpBlueprintInfo(expBlueprintId);
		expi.addExpd(expdId);
		expBlueprintInfoRepository.saveAndFlush(expi);
		log.debug("Added ExpD " + expdId + " to blueprint " + expBlueprintId);
	}
	
	public synchronized void removeExpdInBlueprint(String expBlueprintId, String expdId)
			throws NotExistingEntityException {
		log.debug("Removing EXPD " + expdId + " from blueprint " + expBlueprintId);
		ExpBlueprintInfo expi = getExpBlueprintInfo(expBlueprintId);
		expi.removeExpd(expdId);;
		expBlueprintInfoRepository.saveAndFlush(expi);
		log.debug("Removed ExpD " + expdId + " from blueprint " + expBlueprintId);
	}
	

	private ExpBlueprint getExpBlueprint(String name, String version) throws NotExistingEntityException {
		Optional<ExpBlueprint> expBlueprintOpt = expBlueprintRepository.findByNameAndVersion(name, version);
		if (expBlueprintOpt.isPresent()) return expBlueprintOpt.get();
		else throw new NotExistingEntityException("Experiment Blueprint with name " + name + " and version " + version + " not found in DB.");
	}
	
	private ExpBlueprintInfo getExpBlueprintInfo(String name, String version) throws NotExistingEntityException {
		ExpBlueprintInfo expBlueprintInfo;
		Optional<ExpBlueprintInfo> expbInfoOpt = expBlueprintInfoRepository.findByNameAndExpBlueprintVersion(name, version);
		if (expbInfoOpt.isPresent()) expBlueprintInfo = expbInfoOpt.get();
		else throw new NotExistingEntityException("Experiment Blueprint info with name " + name + " and version " + version + " not found in DB.");
		ExpBlueprint expb = getExpBlueprint(name, version);
		expBlueprintInfo.setExpBlueprint(expb);
		return expBlueprintInfo;
	}
	
	private ExpBlueprint getExpBlueprint(String expbId) throws NotExistingEntityException {
		Optional<ExpBlueprint> expBlueprintOpt = expBlueprintRepository.findByExpBlueprintId(expbId);
		if (expBlueprintOpt.isPresent()) return expBlueprintOpt.get();
		else throw new NotExistingEntityException("Experiment Blueprint with ID " + expbId + " not found in DB.");
	}
	
	private ExpBlueprintInfo getExpBlueprintInfo(String expbId) throws NotExistingEntityException {
		ExpBlueprintInfo expBlueprintInfo;
		Optional<ExpBlueprintInfo> expbInfoOpt = expBlueprintInfoRepository.findByExpBlueprintId(expbId);
		if (expbInfoOpt.isPresent()) expBlueprintInfo = expbInfoOpt.get();
		else throw new NotExistingEntityException("Experiment Blueprint info for ExpB with ID " + expbId + " not found in DB.");
		ExpBlueprint expb = getExpBlueprint(expbId);
		expBlueprintInfo.setExpBlueprint(expb);
		return expBlueprintInfo;
	}
	
	private List<ExpBlueprintInfo> getExpBlueprintInfoFromVsb(String vsbId) throws NotExistingEntityException {
		log.debug("Searching for experiment blueprints associated to VSB " + vsbId);
		List<ExpBlueprint> expbs = expBlueprintRepository.findByVsBlueprintId(vsbId);
		if (expbs.isEmpty()) throw new NotExistingEntityException("Not found any experiment blueprint assocaited to vertical service blueprint " + vsbId);
		List<ExpBlueprintInfo> expbis = new ArrayList<>();
		for (ExpBlueprint expb : expbs) {
			String expbId = expb.getExpBlueprintId();
			ExpBlueprintInfo expbi = expBlueprintInfoRepository.findByExpBlueprintId(expbId).get();
			expbi.setExpBlueprint(expb);
			expbis.add(expbi);
			log.debug("Added experiment blueprint with ID " + expbId);
		}
		return expbis;
	}
	
	private List<ExpBlueprintInfo> getAllExpBlueprintInfos() throws NotExistingEntityException {
		List<ExpBlueprintInfo> expBInfos = expBlueprintInfoRepository.findAll();
		for (ExpBlueprintInfo ebi : expBInfos) {
			ExpBlueprint eb = getExpBlueprint(ebi.getExpBlueprintId());
			ebi.setExpBlueprint(eb);
		}
		return expBInfos;
	}

	private String storeExpBlueprint(ExpBlueprint expBlueprint) throws AlreadyExistingEntityException {

        log.debug("Onboarding EXP blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion());
        if ( (expBlueprintInfoRepository.findByNameAndExpBlueprintVersion(expBlueprint.getName(), expBlueprint.getVersion()).isPresent())) {
            log.error("EXP Blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion() + " already present in DB.");
            throw new AlreadyExistingEntityException("EXP Blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion() + " already present in DB.");
        }
        
        ExpBlueprint target = new ExpBlueprint(expBlueprint.getVersion(),
        		expBlueprint.getName(),
        		expBlueprint.getDescription(),
        		expBlueprint.getSites(),
        		expBlueprint.getVsBlueprintId(),
        		expBlueprint.getCtxBlueprintIds(),
        		expBlueprint.getTcBlueprintIds(),
        		expBlueprint.getMetrics());
        
        expBlueprintRepository.saveAndFlush(target);
        Long expbId = target.getId();
		String expbIdString = String.valueOf(expbId);
		target.setExpBlueprintId(expbIdString);
		expBlueprintRepository.saveAndFlush(target);
		log.debug("Added Experiment Blueprint with ID " + expbIdString);
        
		List<KeyPerformanceIndicator> kpis = expBlueprint.getKpis();
		if (kpis != null) {
			for (KeyPerformanceIndicator kpi : kpis) {
				KeyPerformanceIndicator targetKpi = new KeyPerformanceIndicator(target,
                        kpi.getKpiId(),
                        kpi.getName(),
                        kpi.getFormula(),
                        kpi.getUnit(),                                   
                        kpi.getMetricIds(),
                        kpi.getInterval());
				keyPerformanceIndicatorRepository.saveAndFlush(targetKpi);
				log.debug("Stored kpi " + kpi.getKpiId() + " in experiment blueprint " + expbIdString);
			}
		}
		
        ExpBlueprintInfo expbInfo = new ExpBlueprintInfo(target.getExpBlueprintId(), target.getVersion(), target.getName());
        expBlueprintInfoRepository.saveAndFlush(expbInfo);
        log.debug("Added Experiment Blueprint Info with ID " + expbIdString);

        
        return expbIdString;
    }
	
	/**
	 * Verify if the vertical service, context and test case blueprints defined in the given experiment blueprint are available in the DB.
	 * 
	 * @param expBlueprint input
	 * @throws NotExistingEntityException if one or more blueprints are not available in the DB.
	 */
	private void verifyExperimentBlueprintDependencies(ExpBlueprint expBlueprint) throws NotExistingEntityException {
		log.debug("Verifying dependencies for experiment blueprint.");
		String vsbId = expBlueprint.getVsBlueprintId();
		if (!((vsBlueprintRepository.findByBlueprintId(vsbId)).isPresent())) throw new NotExistingEntityException("VSB with ID " + vsbId + " not present in DB.");
		if (!((vsBlueprintInfoRepository.findByVsBlueprintId(vsbId)).isPresent())) throw new NotExistingEntityException("VSB info with ID " + vsbId + " not present in DB.");
		List<String> ctxBlueprintIds = expBlueprint.getCtxBlueprintIds();
		for (String cb : ctxBlueprintIds) {
			if (!(ctxBlueprintRepository.findByBlueprintId(cb).isPresent())) throw new NotExistingEntityException("CTXB with ID " + cb + " not present in DB.");
			if (!(ctxBlueprintInfoRepository.findByCtxBlueprintId(cb).isPresent())) throw new NotExistingEntityException("CTXB info with ID " + cb + " not present in DB.");
		}
		List<String> tcBlueprintIds = expBlueprint.getTcBlueprintIds();
		for (String tc : tcBlueprintIds) {
			if (!(testCaseBlueprintRepository.findByTestcaseBlueprintId(tc).isPresent())) throw new NotExistingEntityException("TCB with ID " + tc + " not present in DB.");
			if (!(testCaseBlueprintInfoRepository.findByTestCaseBlueprintId(tc).isPresent())) throw new NotExistingEntityException("TCB info with ID " + tc + " not present in DB.");
		}
		log.debug("Verified dependencies for experiment blueprint.");
	}

}
