package it.nextworks.nfvmano.catalogue.blueprint.interfaces;

import it.nextworks.nfvmano.catalogue.arbitrator.im.ArbitratorPolicy;
import it.nextworks.nfvmano.catalogue.arbitrator.im.ArbitratorPolicyInfo;
import it.nextworks.nfvmano.catalogue.arbitrator.messages.OnboardArbitratorPolicyRequest;
import it.nextworks.nfvmano.catalogue.arbitrator.messages.QueryArbitrationPolicyResponse;

import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;

import java.util.List;

public interface ArbitratorPolicyCatalogueInterface {

    /**
     * Method to onboard a Arbitration Policy .
     * @param request
     * @return
     * @throws MethodNotImplementedException
     * @throws MalformattedElementException
     * @throws FailedOperationException
     */
    public List<String> onboardArbitrationPolicy(OnboardArbitratorPolicyRequest request)
            throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException;




    /**
            * Method to retrieve a Arbitration Policy  from the catalogue.
            *
            * @param request query
     * @return the Arbitration Policies
     * @throws MethodNotImplementedException if the method is not implemented
     * @throws MalformattedElementException if the request is malformatted
     * @throws FailedOperationException if the operation fails
     */
    public ArbitratorPolicy getArbitrationPolicy(String policyId);
    /**
     * Method to retrieve a Arbitration Policy Infos from the catalogue.
     *
     * @param request query
     * @return the Arbitration Policies
     * @throws MethodNotImplementedException if the method is not implemented
     * @throws MalformattedElementException if the request is malformatted
     * @throws FailedOperationException if the operation fails
     */
    public QueryArbitrationPolicyResponse queryArbitrationPolicy(GeneralizedQueryRequest request)
            throws MethodNotImplementedException, MalformattedElementException, FailedOperationException;

    /**
     *
     * @throws MethodNotImplementedException
     * @throws MalformattedElementException
     * @throws AlreadyExistingEntityException
     * @throws FailedOperationException
     */
    public void updateArbitrationPolicy(OnboardArbitratorPolicyRequest request)
            throws MalformattedElementException;


    /**
     * Method to delete a arbitration policy with specific id.
     *
     * @param policyId
     * @throws FailedOperationException if the operation fails
     */
    public void deleteArbitrationPolicy(String policyId)
            throws FailedOperationException;


    public ArbitratorPolicyInfo getArbitratorPolicyForRequest(String tenant, String vsbId, String nsServiceType)
            throws FailedOperationException;
}
