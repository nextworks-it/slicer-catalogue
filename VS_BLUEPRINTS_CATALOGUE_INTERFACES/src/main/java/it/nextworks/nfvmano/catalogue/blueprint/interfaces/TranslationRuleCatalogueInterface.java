package it.nextworks.nfvmano.catalogue.blueprint.interfaces;

import it.nextworks.nfvmano.catalogue.blueprint.messages.OnBoardTranslationRuleRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryTranslationRuleResponse;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;

import java.util.List;

public interface TranslationRuleCatalogueInterface {

    /**
     * Method to onboard a set of translation rules.
     * @param request
     * @return
     * @throws MethodNotImplementedException
     * @throws MalformattedElementException
     * @throws FailedOperationException
     */
    public List<String> onBoardTranslationRule(OnBoardTranslationRuleRequest request)
            throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException;

    /**
     * Method to retrieve a Translation Rule from the catalogue.
     *
     * @param request query
     * @return the Translation Rule
     * @throws MethodNotImplementedException if the method is not implemented
     * @throws MalformattedElementException if the request is malformatted
     * @throws FailedOperationException if the operation fails
     */
    public QueryTranslationRuleResponse queryTranslationRule(GeneralizedQueryRequest request)
            throws MethodNotImplementedException, MalformattedElementException, FailedOperationException;

    /**
     *
     * @throws MethodNotImplementedException
     * @throws MalformattedElementException
     * @throws AlreadyExistingEntityException
     * @throws FailedOperationException
     */
    public void updateTranslationRule(OnBoardTranslationRuleRequest request)
            throws MalformattedElementException;


    /**
     * Method to delete a translation rule with specific id.
     *
     * @param ruleId
     * @throws FailedOperationException if the operation fails
     */
    public void deleteTranslationRule(String ruleId)
            throws FailedOperationException;
}
