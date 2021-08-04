package it.nextworks.nfvmano.catalogue.arbitrator.services;

import it.nextworks.nfvmano.catalogue.arbitrator.im.ArbitratorPolicyFilter;
import it.nextworks.nfvmano.catalogue.arbitrator.im.ArbitratorPolicySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.nextworks.nfvmano.catalogue.arbitrator.im.ArbitratorPolicy;
import it.nextworks.nfvmano.catalogue.arbitrator.im.ArbitratorPolicyInfo;
import it.nextworks.nfvmano.catalogue.arbitrator.messages.OnboardArbitratorPolicyRequest;
import it.nextworks.nfvmano.catalogue.arbitrator.messages.QueryArbitrationPolicyResponse;
import it.nextworks.nfvmano.catalogue.arbitrator.repo.ArbitratorPolicyInfoRepo;
import it.nextworks.nfvmano.catalogue.arbitrator.repo.ArbitratorPolicyRepo;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.ArbitratorPolicyCatalogueInterface;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Service
public class ArbitratorPolicyService implements ArbitratorPolicyCatalogueInterface {

    private static final Logger log = LoggerFactory.getLogger(ArbitratorPolicyService.class);


    @Value("${arbitrator.policy.model:1}")
    private String defaultModel;

    @Autowired
    private ArbitratorPolicyInfoRepo arbitratorPolicyInfoRepo;

    @Autowired
    private ArbitratorPolicyRepo arbitratorPolicyRepo;

    @Override
    public List<String> onboardArbitrationPolicy(OnboardArbitratorPolicyRequest request)
            throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException{

        log.debug("Received request to onboard an Arbitrator Policy:"+request);
        List<String> obtainedIds = new ArrayList<>();
        for(ArbitratorPolicy policy : request.getPolicies()){
            log.debug("Onboarding "+policy.getName());
            ArbitratorPolicy nPolicy = new ArbitratorPolicy(null, policy.getName(), policy.getPolicyUpdateStrategy(), null, policy.getModelId(), policy.getPolicyUpdateStrategyParams(), false);
            arbitratorPolicyRepo.saveAndFlush(nPolicy);

            ArbitratorPolicyFilter arbitratorPolicyFilter = new ArbitratorPolicyFilter(nPolicy,policy.getFilter().getFilterData());
            String arbitratorPolicyId = String.valueOf(nPolicy.getId());
            nPolicy.setArbitratorPolicyId(arbitratorPolicyId);
            nPolicy.setFilter(arbitratorPolicyFilter);
            log.debug("Received ID "+arbitratorPolicyId);
            arbitratorPolicyRepo.saveAndFlush(nPolicy);

            ArbitratorPolicyInfo arbitratorPolicyInfo = new ArbitratorPolicyInfo(arbitratorPolicyId, 0, null, null, nPolicy);
            arbitratorPolicyInfoRepo.saveAndFlush(arbitratorPolicyInfo);
            log.debug("Onboarding "+policy.getName() +"- completed");
            obtainedIds.add(arbitratorPolicyId);
        }
        return obtainedIds;

    }

    @PostConstruct
    private void onboardDefaultPolicy(){
        log.debug("Onboarding default policy");
        ArbitratorPolicy nPolicy = new ArbitratorPolicy(null, "default", null, null, defaultModel,new HashMap<>(), true);
        arbitratorPolicyRepo.saveAndFlush(nPolicy);
        String arbitratorPolicyId = String.valueOf(nPolicy.getId());
        log.debug("Received ID "+arbitratorPolicyId);
        nPolicy.setArbitratorPolicyId(arbitratorPolicyId);
        arbitratorPolicyRepo.saveAndFlush(nPolicy);

        ArbitratorPolicyInfo arbitratorPolicyInfo = new ArbitratorPolicyInfo(arbitratorPolicyId, 0, null, null, nPolicy);
        arbitratorPolicyInfoRepo.saveAndFlush(arbitratorPolicyInfo);

    }



    @Override
    public ArbitratorPolicy getArbitrationPolicy(String policyId){

        return arbitratorPolicyRepo.findByArbitratorPolicyId(policyId).get();

    }




    @Override
    public QueryArbitrationPolicyResponse queryArbitrationPolicy(GeneralizedQueryRequest request)
            throws MethodNotImplementedException, MalformattedElementException, FailedOperationException{

        return new QueryArbitrationPolicyResponse(arbitratorPolicyInfoRepo.findAll());

    }

    @Override
    public ArbitratorPolicyInfo getArbitratorPolicyForRequest(String tenant, String vsbId, String nsServiceType)
            throws FailedOperationException{
        List<ArbitratorPolicy> policies = arbitratorPolicyRepo.findAll();
        for(ArbitratorPolicy policy : policies){
            if(policy.getFilter()!=null&& !policy.getFilter().getFilterData().isEmpty()){
                if(policy.getFilter().getFilterData().containsKey(ArbitratorPolicySelector.VSB)&&
                        policy.getFilter().getFilterData().get(ArbitratorPolicySelector.VSB).equals(vsbId)){
                    log.debug("Found arbitrator policy for VSB_ID:"+ vsbId);
                    return arbitratorPolicyInfoRepo.findByArbitratorPolicyInfoId(policy.getArbitratorPolicyId()).get();
                }else if(policy.getFilter().getFilterData().containsKey(ArbitratorPolicySelector.NETWORK_SLICE_SERVICE_TYPE)&&
                        policy.getFilter().getFilterData().get(ArbitratorPolicySelector.NETWORK_SLICE_SERVICE_TYPE).equals(nsServiceType)){
                    log.debug("Found arbitrator policy for NST:"+ nsServiceType);
                    return arbitratorPolicyInfoRepo.findByArbitratorPolicyInfoId(policy.getArbitratorPolicyId()).get();
                }else if(policy.getFilter().getFilterData().containsKey(ArbitratorPolicySelector.TENANT)&&
                        policy.getFilter().getFilterData().get(ArbitratorPolicySelector.TENANT).equals(tenant)){
                    log.debug("Found arbitrator policy for TENANT:"+ tenant);
                    return arbitratorPolicyInfoRepo.findByArbitratorPolicyInfoId(policy.getArbitratorPolicyId()).get();
                }
            }


        }
        log.debug("using default policy");
        ArbitratorPolicy defP = arbitratorPolicyRepo.findByIsDefaultTrue();

        return arbitratorPolicyInfoRepo.findByArbitratorPolicyInfoId(defP.getArbitratorPolicyId()).get() ;
    }

    @Override
    public void updateArbitrationPolicy(OnboardArbitratorPolicyRequest request)
            throws MalformattedElementException{
        log.debug("Received request to update arbiration policy");

    }


    @Override
    public void deleteArbitrationPolicy(String policyId)
            throws FailedOperationException{
        log.debug("Received request to delete arbiration policy");
    }



    public void updateArbitratorPolicyInfoFile(String policyId, File file){
        log.debug("Received request to update and arbiration policy trained model file");
        ArbitratorPolicyInfo policyInfo = arbitratorPolicyInfoRepo.findByArbitratorPolicyInfoId(policyId).get();
        policyInfo.setTrainedModelFilePath(file.getAbsolutePath());
        arbitratorPolicyInfoRepo.saveAndFlush(policyInfo);

    }

    public String getArbitratorPolicyInfoFile(String policyId){
        ArbitratorPolicyInfo policyInfo = arbitratorPolicyInfoRepo.findByArbitratorPolicyInfoId(policyId).get();
        return new File(policyInfo.getTrainedModelFilePath()).getName();
    }

    public void updateArbitratorPolicyUsage(String policyId){
        log.debug("Received request to update and arbiration policy trained model file");
        ArbitratorPolicyInfo policyInfo = arbitratorPolicyInfoRepo.findByArbitratorPolicyInfoId(policyId).get();
        policyInfo.setTimesUsed(policyInfo.getTimesUsed()+1);
        policyInfo.setLastUpdate(new Date());
        arbitratorPolicyInfoRepo.saveAndFlush(policyInfo);
    }

}
