package it.nextworks.nfvmano.catalogue.domainLayer;

import javax.persistence.Entity;

@Entity
public class NspDomainLayer extends DomainLayer {
    private NspNbiType nspNbiType;

    public NspDomainLayer(){}
    public NspDomainLayer(String domainLayerId, NspNbiType nspNbiType) {
        super(domainLayerId, DomainLayerType.NETWORK_SLICE_PROVIDER);
        this.nspNbiType=nspNbiType;
    }
}
