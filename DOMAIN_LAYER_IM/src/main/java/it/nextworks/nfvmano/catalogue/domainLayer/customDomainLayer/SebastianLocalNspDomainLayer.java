package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayerType;
import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;

import javax.persistence.Entity;

@Entity
public class SebastianLocalNspDomainLayer extends NspDomainLayer {

    //TODO: No parameters needed for the moment. Added as a placeholder.


    public SebastianLocalNspDomainLayer(){}

    public SebastianLocalNspDomainLayer(String domainLayerId){
        super(domainLayerId, NspNbiType.SEBASTIAN, true);
        super.setDomainLayerType(DomainLayerType.NETWORK_SERVICE_PROVIDER);



    }
}
