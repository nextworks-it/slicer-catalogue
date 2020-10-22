package it.nextworks.nfvmano.catalogue.blueprint.services;


import it.nextworks.nfvmano.catalogue.blueprint.interfaces.VsDescriptorCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardVsDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryVsDescriptorResponse;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VsDescriptorCatalogueInteractionHandler {


    private Map<String, VsDescriptorCatalogueInterface> vsdCatalogueDrivers= new HashMap<>();


    public void addVsdCatalogueDriver(String domainId, VsDescriptorCatalogueInterface driver){
        vsdCatalogueDrivers.put(domainId, driver);
    }


    /**
     * Method to create a new VSD
     *
     * @param request
     * @return
     * @throws MethodNotImplementedException
     * @throws MalformattedElementException
     * @throws AlreadyExistingEntityException
     * @throws FailedOperationException
     */
    public String onBoardVsDescriptor(String domainId, OnboardVsDescriptorRequest request)
            throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException{
        if(!vsdCatalogueDrivers.containsKey(domainId))
            throw new FailedOperationException("Unknown VSD catalogue driver for:"+domainId);
        return vsdCatalogueDrivers.get(domainId).onBoardVsDescriptor(request);
    }




    /**
     * Method to request info about an existing VSD
     *
     * @param request
     * @return
     * @throws MethodNotImplementedException
     * @throws MalformattedElementException
     * @throws NotExistingEntityException
     * @throws FailedOperationException
     */
    public QueryVsDescriptorResponse queryVsDescriptor(String domainId, GeneralizedQueryRequest request)
            throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException{
        if(!vsdCatalogueDrivers.containsKey(domainId))
            throw new FailedOperationException("Unknown VSD catalogue driver for:"+domainId);
        return vsdCatalogueDrivers.get(domainId).queryVsDescriptor(request);
    }

    /**
     * Method to remove a VSD
     *
     * @param vsDescriptorId
     * @param tenantId
     * @throws MethodNotImplementedException
     * @throws MalformattedElementException
     * @throws NotExistingEntityException
     * @throws FailedOperationException
     */
    public void deleteVsDescriptor(String domainId, String vsDescriptorId, String tenantId)
            throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException{
        if(!vsdCatalogueDrivers.containsKey(domainId))
            throw new FailedOperationException("Unknown VSD catalogue driver for:"+domainId);
        vsdCatalogueDrivers.get(domainId).deleteVsDescriptor(vsDescriptorId, tenantId);
    }


}
