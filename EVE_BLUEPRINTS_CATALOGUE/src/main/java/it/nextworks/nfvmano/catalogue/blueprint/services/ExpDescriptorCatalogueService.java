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
import it.nextworks.nfvmano.catalogue.blueprint.exceptions.ConflictiveOperationException;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.ExpDescriptorCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardExpDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardTestCaseDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardVsDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryExpDescriptorResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.*;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExpDescriptorCatalogueService implements ExpDescriptorCatalogueInterface {

	private static final Logger log = LoggerFactory.getLogger(ExpDescriptorCatalogueService.class);
	
	@Autowired
	private CtxBlueprintRepository ctxBlueprintRepository;
	
	@Autowired
	private CtxBlueprintInfoRepository ctxBlueprintInfoRepository;

	@Autowired
	private ExpBlueprintRepository expBlueprintRepository;

	@Autowired
	private ExpBlueprintInfoRepository expBlueprintInfoRepository;

	@Autowired
	private ExpDescriptorInfoRepository expDescriptorInfoRepository;
	
	@Autowired
	private ExpBlueprintCatalogueService expBlueprintCatalogueService;
	
	@Autowired
	private TestCaseBlueprintRepository testCaseBlueprintRepository;
	
	@Autowired
	private TestCaseBlueprintInfoRepository testCaseBlueprintInfoRepository;
	
	@Autowired
	private ExpDescriptorRepository expDescriptorRepository;
	
	@Autowired
	private VsBlueprintRepository vsBlueprintRepository;
	
	@Autowired
	private VsBlueprintInfoRepository vsBlueprintInfoRepository;
	
	@Autowired
	private VsDescriptorCatalogueService vsDescriptorCatalogueService;
	
	@Autowired
	private CtxDescriptorCatalogueService ctxDescriptorCatalogueService;
	
	@Autowired
	private TcDescriptorCatalogueService tcDescriptorCatalogueService;

	@Autowired
	private AuthService authService;
	
	@Value("${catalogue.admin}")
	private String adminTenant;
	
    @Override
    public String onboardExpDescriptor(OnboardExpDescriptorRequest request) throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException, NotExistingEntityException {
        log.debug("Processing onboarding experiment descriptor request");
        request.isValid();
        verifyExperimentBlueprintDependencies(request);
        verifyKpiThresholds(request);
        String experimentBlueprintName = expBlueprintRepository.findByExpBlueprintId(request.getExperimentBlueprintId()).get().getName();
        
        //onboard VSD
        String vsdId = onboardVerticalServiceDescriptor(request.getVsDescriptor(), request.getTenantId(), request.isPublic());
        
        //onboad CTXDs
        List<BlueprintUserInformation> contextDetails = request.getContextDetails();
        int i = 0;
        List<String> ctxDIds = new ArrayList<>();
        for (BlueprintUserInformation cd : contextDetails) {
        	String ctxdId = onboardContextDescriptor(cd, request.getTenantId(), request.isPublic(), i, experimentBlueprintName);
        	i++;
        	ctxDIds.add(ctxdId);
        }
        
        //onboad TCDs
        i = 0;
        List<String> tcDIds = new ArrayList<>();
        List<BlueprintUserInformation> tcDetails = request.getTestCaseConfiguration();
        for (BlueprintUserInformation tc : tcDetails) {
        	String tcdId = onboardTestCaseDescriptor(tc, request.getTenantId(), request.isPublic(), i, experimentBlueprintName);
        	i++;
        	tcDIds.add(tcdId);
        }
        
        //onboard experiment descriptor
        ExpDescriptor expDescriptor = new ExpDescriptor(request.getName(), 
        		request.getVersion(),
        		request.getExperimentBlueprintId(),
        		request.isPublic(),
        		request.getTenantId(),
        		vsdId,
        		ctxDIds,
        		tcDIds,
    			request.getKpiThresholds());
        
        expDescriptorRepository.saveAndFlush(expDescriptor);
        String idStr = String.valueOf(expDescriptor.getId());
        expDescriptor.setExpDescriptorId(idStr);
        expDescriptorRepository.saveAndFlush(expDescriptor);
		log.debug("Added Experiment Descriptor with ID " + idStr);
		
		try {
			expBlueprintCatalogueService.addExpdInBlueprint(request.getExperimentBlueprintId(), idStr);
		} catch (NotExistingEntityException e) {
			throw new FailedOperationException(e.getMessage());
		}

		log.debug("Storing ExpD associated information element");
		ExpDescriptorInfo expdInfo = new ExpDescriptorInfo(idStr, expDescriptor.getName(), expDescriptor.getVersion());
		expDescriptorInfoRepository.saveAndFlush(expdInfo);
    	return idStr;
    }

    private void verifyKpiThresholds(OnboardExpDescriptorRequest request) throws MalformattedElementException {
        log.debug("Verifiying KPI thresholds");

        if(request.getKpiThresholds()!=null && !request.getKpiThresholds().isEmpty()){
            ExpBlueprint expBlueprint = expBlueprintRepository.findByExpBlueprintId(request.getExperimentBlueprintId()).get();
            if(expBlueprint.getKpis()!=null && !expBlueprint.getKpis().isEmpty()){
                List<String> expbKpis = expBlueprint.getKpis().stream()
                        .map(expbK -> expbK.getKpiId())
                        .collect(Collectors.toList());
                for(String kpiId : request.getKpiThresholds().keySet()){
                    if(!expbKpis.contains(kpiId))
                        throw new MalformattedElementException("KPI " +kpiId+" not defined for the experiment");
                }
            }else throw new MalformattedElementException("KPI threshold for experiment without KPIs");


        }


    }

    @Override
    public QueryExpDescriptorResponse queryExpDescriptor(GeneralizedQueryRequest request) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
    	log.debug("Processing a query for an Experiment descriptor");
		request.isValid();

		//At the moment the only filters accepted are:
		//1. Experiment Descriptor ID and tenant ID
		//EXPD_ID & TENANT_ID
		//2. Tenant ID
		//TENANT_ID
		//No attribute selector is supported at the moment
		
		List<ExpDescriptor> expDescriptors = new ArrayList<>();
		Filter filter = request.getFilter();
        List<String> attributeSelector = request.getAttributeSelector();
        boolean siteAdmin = authService.getUserRoles().contains("SiteManager");
        if ((attributeSelector == null) || (attributeSelector.isEmpty())) {
        	Map<String, String> fp = filter.getParameters();
            if (fp.size() == 1 && fp.containsKey("TENANT_ID")) {
            	String tenantId = fp.get("TENANT_ID");
            	if (tenantId.equals(adminTenant)) {
            		log.debug("ExpD query for admin: returning all the ExpDs");
            		expDescriptors = expDescriptorRepository.findAll();
            		log.debug("Retrieved all the ExpDs");
            	} else {
            		expDescriptors = expDescriptorRepository.findByTenantId(tenantId);
            		log.debug("Added ExpD for tenant " + tenantId);
            		List<ExpDescriptor> tmpPublicExpds = expDescriptorRepository.findByIsPublic(true);
            		for (ExpDescriptor expd : tmpPublicExpds) {
            			if (!(expd.getTenantId().equals(tenantId))) {
            				expDescriptors.add(expd);
            				log.debug("Added public ExpD " + expd.getName());
            			}
            		}
            	}
            } else if (fp.size() == 2 && fp.containsKey("EXPD_ID") && fp.containsKey("TENANT_ID")) {
            	String expdId = fp.get("EXPD_ID");
            	String tenantId = fp.get("TENANT_ID");
            	Optional<ExpDescriptor> expd = null;
            	if (tenantId.equals(adminTenant)|| siteAdmin) {
            		expd = expDescriptorRepository.findByExpDescriptorId(expdId);
            	} else {
            		expd = expDescriptorRepository.findByExpDescriptorIdAndTenantId(expdId, tenantId);
            	}
            	if (expd.isPresent()) {
            		expDescriptors.add(expd.get());
            		log.debug("Added EXPD with EXPD ID " + expdId + " for tenant " + tenantId);
            	} else throw new NotExistingEntityException("EXPD with EXPD ID " + expdId + " for tenant " + tenantId + " not found");
            } else if (fp.isEmpty()) {
            	expDescriptors = expDescriptorRepository.findByIsPublic(true);
            	log.debug("Added all the public VSD available in DB.");
            }
            return new QueryExpDescriptorResponse(expDescriptors);
        } else {
            log.error("Received query EXP Descriptor with attribute selector. Not supported at the moment.");
            throw new MethodNotImplementedException("Received query EXP Descriptor with attribute selector. Not supported at the moment.");
        }
    }



    public void deleteExpDescriptor(String expDescriptorId, String tenantId) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException, NotPermittedOperationException, ConflictiveOperationException {
    	log.debug("Processing request to delete an Exp descriptor");


		//TODO: to be updated
		boolean catalogueAdmin = false;
		boolean forceRemove = false;

    	if  (expDescriptorId == null) throw new MalformattedElementException("ExpD ID is null");
		
		Optional<ExpDescriptor> expdOpt = expDescriptorRepository.findByExpDescriptorId(expDescriptorId);
		if (expdOpt.isPresent()) {
			ExpDescriptor expd = expdOpt.get();
			if ( catalogueAdmin || expd.getTenantId().equals(tenantId) ) {
				Optional<ExpDescriptorInfo> optExpDInfo = expDescriptorInfoRepository.findByExpDescriptorId(expDescriptorId);
				if(optExpDInfo.isPresent()){
					List<String> activeExperiments =optExpDInfo.get().getActiveExperimentIds();
					if( activeExperiments!=null && !activeExperiments.isEmpty()&& !forceRemove){

						throw new ConflictiveOperationException("ExpD "+expDescriptorId+" still has active experiments: "+activeExperiments);
					}
				}else throw new FailedOperationException("Failed to retrieve associated ExpDInfo for:"+expDescriptorId);

				String vsDescriptorId = expd.getVsDescriptorId();
				vsDescriptorCatalogueService.deleteVsDescriptor(vsDescriptorId, tenantId);
				log.debug("Removing CTXDs in EXPD " + expDescriptorId);
				List<String> ctxdIds = expd.getCtxDescriptorIds();
				for (String ctxDescriptorId : ctxdIds) {
					ctxDescriptorCatalogueService.deleteCtxDescriptor(ctxDescriptorId, tenantId);
				}
				log.debug("Removing TCDs in EXPD " + expDescriptorId);
				List<String> tcdIds = expd.getTestCaseDescriptorIds();
				for (String tcdId : tcdIds) {
					tcDescriptorCatalogueService.deleteTestCaseDescriptor(tcdId, tenantId);
				}
				String expbId = expd.getExpBlueprintId();
				expDescriptorRepository.delete(expd);
				expBlueprintCatalogueService.removeExpdInBlueprint(expbId, expDescriptorId);
				log.debug("EXPD " + expDescriptorId + " removed from the internal DB.");

				log.debug("Removing ExpD associated info element:" + expDescriptorId);
				expDescriptorInfoRepository.delete(optExpDInfo.get());


			} else {
				log.error("Tenant " + tenantId + " does not have the right to remove the ExpD " + expDescriptorId);
				throw new NotPermittedOperationException("Tenant " + tenantId + " does not have the right to remove the ExpD " + expDescriptorId);
			}
		} else {
			log.error("ExpD " + expDescriptorId + " not found");
			throw new NotExistingEntityException("ExpD " + expDescriptorId + " not found");
		}
    }


    
    /**
     * Onboards the given VSD and returns its ID
     * 
     * @param vsd
     * @param tenantId
     * @param isPublic
     * @return the ID of the onboarded VSD
     */
    private String onboardVerticalServiceDescriptor(VsDescriptor vsd, String tenantId, boolean isPublic) 
    		throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
    	log.debug("Onboarding VSD associated to VSB " + vsd.getVsBlueprintId());
    	OnboardVsDescriptorRequest request = new OnboardVsDescriptorRequest(vsd, tenantId, isPublic);
    	String vsdId = vsDescriptorCatalogueService.onBoardVsDescriptor(request);
    	log.debug("Onboarded VSD with ID " + vsdId);
    	return vsdId;
    }
    
    /**
     * Onboards a CTXD with the given parameters and returns its ID
     * 
     * @param info
     * @param tenantId
     * @param isPublic
     * @return the ID of the onboarded CTXD
     */
    private String onboardContextDescriptor(BlueprintUserInformation info, String tenantId, boolean isPublic, int index, String blueprintName) 
    		throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
    	log.debug("Onboarding CTXD associated to CTXB " + info.getBlueprintId());
    	CtxDescriptor ctxd = new CtxDescriptor(blueprintName + "-ctx-" + index, "0.1", info.getBlueprintId(), info.getParameters(), isPublic, tenantId);
    	OnboardCtxDescriptorRequest request = new OnboardCtxDescriptorRequest(ctxd, tenantId, isPublic);
    	String ctxDId = ctxDescriptorCatalogueService.onboardCtxDescriptor(request);
    	log.debug("Onboarded CTXD with ID " + ctxDId);
    	return ctxDId;
    }
    
    /**
     * Onboards a TCD with the given parameters and returns its ID
     * 
     * @param info
     * @param tenantId
     * @param isPublic
     * @return the ID of the onboarded TCD
     */
    private String onboardTestCaseDescriptor(BlueprintUserInformation info, String tenantId, boolean isPublic, int index, String blueprintName) 
    		throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
    	log.debug("Onboarding TCD associated to TCB " + info.getBlueprintId());
    	TestCaseDescriptor testCaseDescriptor = new TestCaseDescriptor(null, blueprintName + "-tc-" + index, "0.1", info.getBlueprintId(), info.getParameters(), isPublic, tenantId);
    	OnboardTestCaseDescriptorRequest request = new OnboardTestCaseDescriptorRequest(testCaseDescriptor, tenantId, isPublic);
    	String tcdId = tcDescriptorCatalogueService.onboardTestCaseDescriptor(request);
    	return tcdId;
    }
    
    /**
     * Verify if all the related blueprints are available in the DB and the provided parameters match with the ones in the blueprints
     * 
     * @param request input
     * @throws NotExistingEntityException
     */
    private void verifyExperimentBlueprintDependencies(OnboardExpDescriptorRequest request) throws NotExistingEntityException {
    	log.debug("Verifying dependencies for experiment descriptor.");
    	//verify experiment blueprint presence
    	String experimentBlueprintId = request.getExperimentBlueprintId();
    	if (!((expBlueprintRepository.findByExpBlueprintId(experimentBlueprintId)).isPresent())) throw new NotExistingEntityException("ExpB with ID " + experimentBlueprintId + " not present in DB.");
    	if (!((expBlueprintInfoRepository.findByExpBlueprintId(experimentBlueprintId)).isPresent())) throw new NotExistingEntityException("ExpB info with ID " + experimentBlueprintId + " not present in DB.");
    	
    	//verify vertical service blueprint presence
    	VsDescriptor vsd = request.getVsDescriptor();
    	String vsBlueprintId = vsd.getVsBlueprintId();
    	if (!((vsBlueprintRepository.findByBlueprintId(vsBlueprintId)).isPresent())) throw new NotExistingEntityException("VS blueprint with ID " + vsBlueprintId + " not present in DB.");
    	if (!((vsBlueprintInfoRepository.findByVsBlueprintId(vsBlueprintId)).isPresent())) throw new NotExistingEntityException("VS blueprint info with ID " + vsBlueprintId + " not present in DB.");
    	
    	//verify matching of VSB ID
    	ExpBlueprint expb = expBlueprintRepository.findByExpBlueprintId(experimentBlueprintId).get();
    	if (!(expb.getVsBlueprintId().equals(vsBlueprintId))) throw new NotExistingEntityException("VSB in VSD is different from the VSB in ExpB");
    	
    	//verify compatibility between VSD parameters and VSB ones
    	Map<String,String> vsdParameters = vsd.getQosParameters();
    	if (vsdParameters != null) {
    		Set<String> vsdParameterIds = vsdParameters.keySet();
    		VsBlueprint vsb = vsBlueprintRepository.findByBlueprintId(vsBlueprintId).get();
    		if (!(vsb.isCompatibleWithDescriptorParameters(vsdParameterIds))) throw new NotExistingEntityException("VSD parameters are not compatible with the ones defined in the VSB");
    	}
    	log.debug("Verified vertical service dependencies");
    	
    	//verify context blueprint presence
    	List<BlueprintUserInformation> contextDetails = request.getContextDetails();
    	for (BlueprintUserInformation ctx : contextDetails) {
    		String ctxBId = ctx.getBlueprintId();
    		if (!((ctxBlueprintInfoRepository.findByCtxBlueprintId(ctxBId)).isPresent())) throw new NotExistingEntityException("CTX blueprint info with ID " + ctxBId + " not present in DB.");
    		if (!((ctxBlueprintRepository.findByBlueprintId(ctxBId)).isPresent())) throw new NotExistingEntityException("CTX blueprint with ID " + ctxBId + " not present in DB.");
    		
    		//verify compatibility between context parameters in request and in blueprints
    		Map<String, String> ctxdParams = ctx.getParameters();
    		if (ctxdParams != null) {
    			Set<String> ctxdParameterIds = ctxdParams.keySet();
    			CtxBlueprint ctxb = ctxBlueprintRepository.findByBlueprintId(ctxBId).get();
    			if (!(ctxb.isCompatibleWithDescriptorParameters(ctxdParameterIds))) throw new NotExistingEntityException("CTX parameters are not compatible with the ones defined in the CTXB");
    		}
    	}
    	log.debug("Verified context dependencies");
    	
    	//verify test case blueprint presence
    	List<BlueprintUserInformation> testCaseConfiguration = request.getTestCaseConfiguration();
    	for (BlueprintUserInformation tcc : testCaseConfiguration) {
    		String tcBId = tcc.getBlueprintId();
    		if (!((testCaseBlueprintInfoRepository.findByTestCaseBlueprintId(tcBId)).isPresent())) throw new NotExistingEntityException("Test case blueprint info with ID " + tcBId + " not present in DB.");
    		if (!((testCaseBlueprintInfoRepository.findByTestCaseBlueprintId(tcBId)).isPresent())) throw new NotExistingEntityException("Test case blueprint with ID " + tcBId + " not present in DB.");
    		
    		//verify compatibility between test case parameters in request and in blueprints
    		Map<String, String> tcdParams = tcc.getParameters();
    		if (tcdParams != null) {
    			Set<String> tcdParameterIds = tcdParams.keySet();
    			TestCaseBlueprint tcb = testCaseBlueprintRepository.findByTestcaseBlueprintId(tcBId).get();
    			if (!(tcb.isCompatibleWithDescriptorParameters(tcdParameterIds))) throw new NotExistingEntityException("Test Case parameters are not compatible with the ones defined in the TCB");
    		}
    	}
    	log.debug("Verified test case dependencies");
    	
    	log.debug("Verified all the dependencies of the descriptor");
    	
    }



	@Override
    public void useExpDescriptor(String expdId, String experimentId)throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException {
		log.debug("Received request to add experiment: "+experimentId+" from ExpD:"+expdId);
		if  (expdId == null || experimentId==null) throw new MalformattedElementException("ExpD ID or experiment is null");

		Optional<ExpDescriptorInfo> expdInfoOpt = expDescriptorInfoRepository.findByExpDescriptorId(expdId);
		if(expdInfoOpt.isPresent()){
			ExpDescriptorInfo expDInfo = expdInfoOpt.get();
			List<String> activeExperimentIds = expDInfo.getActiveExperimentIds();
			if(!activeExperimentIds.contains(experimentId)){
				activeExperimentIds.add(experimentId);
				expDInfo.setActiveExperimentIds(activeExperimentIds);
				expDescriptorInfoRepository.saveAndFlush(expDInfo);
				log.debug("Correctly added experiment:"+experimentId+" from ExpD:"+expdId);

			}else{
				log.warn("Experiment ID: "+experimentId+" already in the list of ExpD: "+ expdId+"active experiments, ignoring");
			}


		}else throw  new NotExistingEntityException("Could not find ExpD Info with id"+expdId);

	}

	@Override
	public void releaseExpDescriptor(String expdId, String experimentId) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException {
		log.debug("Received request to remove experiment: "+experimentId+" from ExpD:"+expdId);
		if  (expdId == null) throw new MalformattedElementException("ExpD ID is null");

		Optional<ExpDescriptorInfo> expdInfoOpt = expDescriptorInfoRepository.findByExpDescriptorId(expdId);
    	if(expdInfoOpt.isPresent()){
    		ExpDescriptorInfo expDInfo = expdInfoOpt.get();
    		List<String> activeExperimentIds = expDInfo.getActiveExperimentIds();
    		boolean removed = activeExperimentIds.remove(experimentId);
    		if(removed){
    			expDInfo.setActiveExperimentIds(activeExperimentIds);
    			expDescriptorInfoRepository.saveAndFlush(expDInfo);
    			log.debug("Correctly removed experiment:"+experimentId+" from ExpD:"+expdId);
			}else throw new NotExistingEntityException("Failed to remove experiment:"+experimentId+" from ExpD:"+expdId);

		}else throw  new NotExistingEntityException("Could not find ExpD with id"+expdId);
	}







}
