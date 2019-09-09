package it.nextworks.nfvmano.catalogue.blueprint.services;

import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.CtxDescriptorCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryCtxDescriptorResponse;
import it.nextworks.nfvmano.libs.common.exceptions.*;
import it.nextworks.nfvmano.libs.common.messages.GeneralizedQueryRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

;

@Service
public class CtxDescriptorCatalogueService implements CtxDescriptorCatalogueInterface {

    @Override
    public String onboardCtxDescriptor(OnboardCtxDescriptorRequest request) throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
        return null;
    }

    @Override
    public QueryCtxDescriptorResponse queryCtxDescriptor(GeneralizedQueryRequest request) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
        return null;
    }

    @Override
    public void deleteCtxDescriptor(String ctxDescriptorId, String tenantId) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {

    }

    @Override
    public Optional<CtxDescriptor> findByCtxDescriptorId(String ctxDescriptorId) {
        return Optional.empty();
    }
}
