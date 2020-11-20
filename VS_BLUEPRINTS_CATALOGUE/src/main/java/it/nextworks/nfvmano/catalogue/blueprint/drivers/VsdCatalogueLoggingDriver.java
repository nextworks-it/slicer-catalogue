package it.nextworks.nfvmano.catalogue.blueprint.drivers;

import it.nextworks.nfvmano.catalogue.blueprint.interfaces.VsDescriptorCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardVsDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryVsDescriptorResponse;
import it.nextworks.nfvmano.catalogue.blueprint.services.VsDescriptorCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class VsdCatalogueLoggingDriver implements VsDescriptorCatalogueInterface {

    private static final Logger log = LoggerFactory.getLogger(VsdCatalogueLoggingDriver.class);

    @Override
    public String onBoardVsDescriptor(OnboardVsDescriptorRequest request) throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
        log.debug("Requested to onboard VSD");
        String id = UUID.randomUUID().toString();
        log.debug("generated id:"+ id);
        return id;
    }

    @Override
    public QueryVsDescriptorResponse queryVsDescriptor(GeneralizedQueryRequest request) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
        log.debug("QueryVSD");

        return null;
    }

    @Override
    public void deleteVsDescriptor(String vsDescriptorId, String tenantId) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
        log.debug("Delete VSD");
    }
}
