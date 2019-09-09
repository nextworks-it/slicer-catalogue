package it.nextworks.nfvmano.catalogue.blueprint.services;

import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsDescriptor;
;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.ExpDescriptorCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardExpDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryExpDescriptorResponse;
import it.nextworks.nfvmano.libs.common.exceptions.*;
import it.nextworks.nfvmano.libs.common.messages.GeneralizedQueryRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpDescriptorCatalogueService implements ExpDescriptorCatalogueInterface {

    @Override
    public String onboardExpDescriptor(OnboardExpDescriptorRequest request) throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
        return null;
    }

    @Override
    public QueryExpDescriptorResponse queryExpDescriptor(GeneralizedQueryRequest request) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
        return null;
    }

    @Override
    public void deleteExpDescriptor(String expDescriptorId, String tenantId) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {

    }

    @Override
    public Optional<ExpDescriptor> findByExpDescriptorId(String expDescriptorId) {
        return Optional.empty();
    }
}
