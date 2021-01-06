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
import it.nextworks.nfvmano.libs.ifa.catalogues.interfaces.elements.NsdInfo;
import it.nextworks.nfvmano.libs.ifa.catalogues.interfaces.messages.*;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;

import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.*;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
	private AuthService authService;


	public ExpBlueprintCatalogueService() {	}
	
	@Override
	public synchronized String onboardExpBlueprint(OnboardExpBlueprintRequest request)
            throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException, NotExistingEntityException {
		log.debug("Processing request to onboard a new Exp blueprint");
		request.isValid();
		ExpBlueprint expB = request.getExpBlueprint();
        verifyExperimentBlueprintDependencies(expB, request.getContextComponent());
        Map<String, Nsd> nsdInfoIdNsd =  storeNsds(request);

        String experimentId = storeExpBlueprint(expB, request.getOwner(), nsdInfoIdNsd, request.getContextComponent());
        
        ExpBlueprintInfo expBlueprintInfo;
		try {
			expBlueprintInfo = getExpBlueprintInfo(experimentId);
		} catch (NotExistingEntityException e) {
			log.error("Impossible to retrieve expBlueprintInfo. Error!");
			throw new FailedOperationException("Internal error: impossible to retrieve expBlueprintInfo.");
		}

		storeTranslationRules(request, expBlueprintInfo, nsdInfoIdNsd);
		return experimentId;
			

		
	}




	private void storeTranslationRules(OnboardExpBlueprintRequest request, ExpBlueprintInfo expBlueprintInfo, Map<String, Nsd> nsdInfoIdNsd) throws MalformattedElementException {
		log.debug("Storing Translation rules");

		String expbId = expBlueprintInfo.getExpBlueprintId();
		request.setBlueprintIdInTranslationRules(expbId);
		if(nsdInfoIdNsd!=null && !nsdInfoIdNsd.isEmpty()){
			log.debug("Adding nsdInfoId in translation rules");
			for(Map.Entry<String, Nsd> entry:  nsdInfoIdNsd.entrySet()){
				request.setNsdInfoIdInTranslationRules(entry.getKey(), entry.getValue().getNsdIdentifier(), entry.getValue().getVersion());
			}
		}

		createDefaultTranslationRule(request, expBlueprintInfo);

		List<VsdNsdTranslationRule> trs = request.getTranslationRules();
		for (VsdNsdTranslationRule tr : trs) {
			if(request.getEnhancedVsbs()!=null && !request.getEnhancedVsbs().isEmpty()){
				Map<String, VsdNestedNsdTranslation> nestedNsdTranslationMap= computeVsdNestedNsdTranslation(request, tr);
				tr.setVsdNestedNsdTranslations(nestedNsdTranslationMap);
			}
			translationRuleRepository.saveAndFlush(tr);
		}
		log.debug("Translation rules saved in internal DB.");
		expBlueprintInfoRepository.saveAndFlush(expBlueprintInfo);
	}

	private Map<String, VsdNestedNsdTranslation> computeVsdNestedNsdTranslation(OnboardExpBlueprintRequest request, VsdNsdTranslationRule tr) throws MalformattedElementException {
		log.debug("computing Nested VS translation rules");
		//This method creates theVsdNestedNsdTranslation which over-rides the translation
		//for the vertical sub-service. The information is retrieved from the translation rules and
		//the composite NSD


		Map<String, Nsd> enhancedVsbs = request.getEnhancedVsbs();
		Map<String, VsdNestedNsdTranslation> translationMap = new HashMap<>();
		Nsd defaultNsd = request.getNsds().get(request.getNsds().size() - 1);
		String compositeDfId = tr.getNsFlavourId();
		String compositeIlId = tr.getNsInstantiationLevelId();
		try {
			NsDf compositeDf = defaultNsd.getNsDeploymentFlavour(compositeDfId);
			List<NsProfile> nsProfiles = compositeDf.getNsProfile();

			NsLevel compositeIl = compositeDf.getNsLevel(compositeIlId);
			for(String componentId : enhancedVsbs.keySet()){
				String nestedNsdId = enhancedVsbs.get(componentId).getNsdIdentifier();
				NsProfile nsProfile = nsProfiles.stream()
							.filter(p -> p.getNsdId().equals(nestedNsdId))
							.findFirst()
							.get();
				if(nsProfile==null)
					throw new MalformattedElementException("Could not find a NS profile for:"+nestedNsdId);
				NsToLevelMapping nsToLevelMapping= compositeIl.getNsToLevelMapping().stream()
												.filter(l -> l.getNsProfileId().equals(nsProfile.getNsProfileId()))
												.findFirst()
												.get();
				if(nsToLevelMapping==null)
					throw new MalformattedElementException("NS Profile not mapped in instantiation level"+nsProfile.getNsProfileId()+" "+compositeIlId);
				VsdNestedNsdTranslation nestedNsdTranslation = new VsdNestedNsdTranslation(nestedNsdId, nsProfile.getNsDeploymentFlavourId(), nsProfile.getNsInstantiationLevelId());
				translationMap.put(componentId, nestedNsdTranslation);

			}

		} catch (NotExistingEntityException e) {
			throw new MalformattedElementException(e);
		}


		return translationMap;
	}

	private void createDefaultTranslationRule(OnboardExpBlueprintRequest request, ExpBlueprintInfo expBlueprintInfo) {
		List<Nsd> nsds = request.getNsds();
		if(nsds!= null && !nsds.isEmpty()){
			log.debug("Creating default translation rule, using the last NSD in the request");

			Nsd defaultNsd = request.getNsds().get(nsds.size() - 1);
			String nsdDf = defaultNsd.getNsDf().get(0).getNsDfId();
			String instantiationLevel =  defaultNsd.getNsDf().get(0).getDefaultNsInstantiationLevelId();
			VsdNsdTranslationRule defaultRule = new VsdNsdTranslationRule("", defaultNsd.getNsdIdentifier(), defaultNsd.getVersion(),nsdDf, instantiationLevel);
			log.debug("Default rule:"+ defaultNsd.getNsdIdentifier()+" "+nsdDf+" "+instantiationLevel);
			defaultRule.setBlueprintId(expBlueprintInfo.getExpBlueprintId());
			translationRuleRepository.saveAndFlush(defaultRule);

		}


	}

	private Map<String, Nsd> storeNsds(OnboardExpBlueprintRequest request) throws FailedOperationException, MethodNotImplementedException, MalformattedElementException {
		log.debug("Storing enhanced VSB NSDs");
		VsBlueprint vsb = vsBlueprintRepository.findByBlueprintId(request.getExpBlueprint().getVsBlueprintId()).get();
		for(String componentId: request.getEnhancedVsbs().keySet() ){
			Optional<VsComponent> component = vsb.getAtomicComponents().stream()
										.filter(c -> c.getComponentId().equals(componentId))
										.findFirst();
			if(component.isPresent()){

				List<EveSite> compatibleSites = new ArrayList<>();
				compatibleSites.add(EveSite.valueOf(component.get().getCompatibleSite()));

				try {
					storeNsd(compatibleSites, request.getEnhancedVsbs().get(componentId), false);
				} catch (AlreadyExistingEntityException e) {
					log.debug("The NSD is already present in the NFVO catalogue. IGNORING.");
				}
			}else throw new MalformattedElementException("Component with ID: "+componentId+" not found on VSB:"+vsb.getBlueprintId()+" while onboarding enhanced VSBs");

		}
		log.debug("Storing NSDs");
		List<Nsd> nsds = request.getNsds();
		Map<String, Nsd> nsdInfoIdNsd = new HashMap<>();
		for (Nsd nsd : nsds) {
			String nsdInfoId = null;
			List<EveSite> sites = request.getExpBlueprint().getSites();
			try {
				boolean interSite = vsb.isInterSite();
				nsdInfoId = this.storeNsd(sites, nsd, interSite);

				} catch (AlreadyExistingEntityException e) {

				log.debug("The NSD is already present in the NFVO catalogue. IGNORING.");
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
		return nsdInfoIdNsd;

	}


	private String storeNsd(List<EveSite> sites, Nsd nsd, boolean interSite) throws MalformattedElementException, FailedOperationException, MethodNotImplementedException, AlreadyExistingEntityException {

			Map<String, String> userDefinedData = new HashMap<>();
			if(!interSite){
				for (EveSite site : sites) {
					userDefinedData.put(site.toString(), "yes");
					if(site==EveSite.FRANCE_RENNES || site==EveSite.FRANCE_NICE|| site==EveSite.FRANCE_PARIS||site==EveSite.FRANCE_SACLAY
							||site==EveSite.FRANCE_CHATILLON||site==EveSite.FRANCE_SOPHIA_ANTIPOLIS||site==EveSite.FRANCE_LANNION){
						log.debug("Adding nsd_invariant_id to userDefinedData:"+nsd.getNsdInvariantId());
						userDefinedData.put("NSD_INVARIANT_ID",nsd.getNsdInvariantId());

					}
				}
			}else{
				log.debug("onboarding inter-site NSDs for EXPB");
				if(nsd.getNestedNsdId()==null || nsd.getNestedNsdId().isEmpty())
					throw new MalformattedElementException("Inter-site VSB with non composite NSD");
				userDefinedData.put("multiSite","yes");

			}
			String nsdInfoId = nfvoCatalogueService.onboardNsd(new OnboardNsdRequest(nsd, userDefinedData));
			log.debug("Added NSD " + nsd.getNsdIdentifier() +
					", version " + nsd.getVersion() + " in NFVO catalogue. NSD Info ID: " + nsdInfoId);

			return nsdInfoId;

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
		List<NsdInfo> nsdInfos = new ArrayList<>();
		
		Filter filter = request.getFilter();
        List<String> attributeSelector = request.getAttributeSelector();
        if ((attributeSelector == null) || (attributeSelector.isEmpty())) {
        	Map<String, String> fp = filter.getParameters();
            if (fp.size() == 1 && fp.containsKey("EXPB_ID")) {
            	String expbId = fp.get("EXPB_ID");
            	ExpBlueprintInfo expb = getExpBlueprintInfo(expbId);
            	expbs.add(expb);
            	/*for(String nsdInfoId : expb.getOnBoardedNsdInfoId()){
					GeneralizedQueryRequest query = new GeneralizedQueryRequest(
							BlueprintCatalogueUtilities.buildNsdInfoFilter(nsdInfoId),
							null);
            		nsdInfos.addAll(nfvoCatalogueService.queryNsd(query).getQueryResult());
				}*/
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
            	/*for(String nsdInfoId : expb.getOnBoardedNsdInfoId()){
					GeneralizedQueryRequest query = new GeneralizedQueryRequest(
							BlueprintCatalogueUtilities.buildNsdInfoFilter(nsdInfoId),
							null);
					nsdInfos.addAll(nfvoCatalogueService.queryNsd(query).getQueryResult());
				}
				*/
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
	public synchronized  void deleteExpBlueprint(String expBlueprintId) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String tenantId = authService.getUserFromAuth(authentication);
		log.debug("Processing request to delete a Experiment blueprint with ID " + expBlueprintId+" by:"+tenantId);

			if (expBlueprintId == null) throw new MalformattedElementException("The Experiment blueprint ID is null");

			ExpBlueprintInfo expbi = getExpBlueprintInfo(expBlueprintId);

			boolean catalogueAdmin = authService.isCatalogueAdminUser(authentication);
			if (!(expbi.getActiveExpdId().isEmpty())) {
				log.error("There are some ExpDs associated to the Experiment Blueprint. Impossible to remove it.");
				throw new FailedOperationException("There are some ExpDs associated to the Experiment Blueprint. Impossible to remove it.");
			}
			if(catalogueAdmin || expbi.getOwner().equals(tenantId)){

				expBlueprintInfoRepository.delete(expbi);
				log.debug("Removed ExpB info from DB.");
				ExpBlueprint expb = getExpBlueprint(expBlueprintId);
				expBlueprintRepository.delete(expb);
				log.debug("Removed ExpB from DB.");
			}else throw new FailedOperationException("Logged user cannot delete the specified ExpB:"+tenantId+" "+expBlueprintId);
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

	private String storeExpBlueprint(ExpBlueprint expBlueprint, String owner, Map<String, Nsd> nsdInfoIdNsd, Map<String, String> contextComponent) throws AlreadyExistingEntityException {

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
        		expBlueprint.getMetrics(),
				expBlueprint.getDeploymentType());
        
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
                        kpi.getInterval(),
						kpi.getKpiGraphType());
				keyPerformanceIndicatorRepository.saveAndFlush(targetKpi);
				log.debug("Stored kpi " + kpi.getKpiId() + " in experiment blueprint " + expbIdString);
			}
		}
		
        ExpBlueprintInfo expbInfo = new ExpBlueprintInfo(target.getExpBlueprintId(), target.getVersion(), target.getName(), owner, contextComponent);
        expBlueprintInfoRepository.saveAndFlush(expbInfo);
        log.debug("Added Experiment Blueprint Info with ID " + expbIdString);

		if(nsdInfoIdNsd!=null && !nsdInfoIdNsd.isEmpty()) {
			log.debug("Adding  VS Blueprint Info nsdInfoIds");
			for (String nsdInfoId : nsdInfoIdNsd.keySet()) {
				expbInfo.addNsdInfoId(nsdInfoId);
			}
			expBlueprintInfoRepository.saveAndFlush(expbInfo);
		}

		return expbIdString;
    }
	
	/**
	 * Verify if the vertical service, context and test case blueprints defined in the given experiment blueprint are available in the DB.
	 * 
	 * @param expBlueprint input
	 * @throws NotExistingEntityException if one or more blueprints are not available in the DB.
	 */
	private void verifyExperimentBlueprintDependencies(ExpBlueprint expBlueprint, Map<String, String> contextComponent ) throws NotExistingEntityException, MalformattedElementException {
		log.debug("Verifying dependencies for experiment blueprint.");
		String vsbId = expBlueprint.getVsBlueprintId();
		Optional<VsBlueprint> vsb = vsBlueprintRepository.findByBlueprintId(vsbId);
		if (!((vsb).isPresent())) throw new NotExistingEntityException("VSB with ID " + vsbId + " not present in DB.");
		if (!((vsBlueprintInfoRepository.findByVsBlueprintId(vsbId)).isPresent())) throw new NotExistingEntityException("VSB info with ID " + vsbId + " not present in DB.");

		List<String> ctxBlueprintIds = expBlueprint.getCtxBlueprintIds();
		for (String cb : ctxBlueprintIds) {
			if (!(ctxBlueprintRepository.findByBlueprintId(cb).isPresent())) throw new NotExistingEntityException("CTXB with ID " + cb + " not present in DB.");
			if (!(ctxBlueprintInfoRepository.findByCtxBlueprintId(cb).isPresent())) throw new NotExistingEntityException("CTXB info with ID " + cb + " not present in DB.");
			boolean containedInMap = contextComponent!=null&& contextComponent.containsKey(cb);
			if (vsb.get().isInterSite() && !containedInMap)
				throw new NotExistingEntityException("Missing context to component mapping for intersite VSB:"+cb);

		}
		List<String> tcBlueprintIds = expBlueprint.getTcBlueprintIds();
		for (String tc : tcBlueprintIds) {
			if (!(testCaseBlueprintRepository.findByTestcaseBlueprintId(tc).isPresent())) throw new NotExistingEntityException("TCB with ID " + tc + " not present in DB.");
			if (!(testCaseBlueprintInfoRepository.findByTestCaseBlueprintId(tc).isPresent())) throw new NotExistingEntityException("TCB info with ID " + tc + " not present in DB.");
		}

		if(vsb.get().isInterSite()){
			boolean expWithCtx = expBlueprint.getCtxBlueprintIds()!=null && !expBlueprint.getCtxBlueprintIds().isEmpty();
			boolean ctxComponentInfo = contextComponent!=null && !contextComponent.isEmpty();
			if(expWithCtx && !ctxComponentInfo )
				throw new MalformattedElementException("Missing context component mapping for intersite experiment");

			for(String ctxbId : expBlueprint.getCtxBlueprintIds() ){
				//if (!expBlueprint.getCtxBlueprintIds().contains(ctxbId)) throw new NotExistingEntityException("CTXB info with ID " + ctxbId + " not present associated with Experiment.");

				if(!contextComponent.containsKey(ctxbId))
					throw new MalformattedElementException("Missing context component mapping for context blueprint with id:"+ctxbId);

				String componentId= contextComponent.get(ctxbId);
				Optional<VsComponent> component = vsb.get().getAtomicComponents().stream()
						.filter(c -> c.getComponentId().equals(componentId))
						.findFirst();
				if(!component.isPresent()) throw new NotExistingEntityException("Could not find component:"+componentId+" in VSB:"+ vsbId);


			}
		}

		log.debug("Verified dependencies for experiment blueprint.");
	}

}
