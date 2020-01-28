package it.nextworks.nfvmano.catalogue.domainLayer;

import javax.persistence.Entity;

@Entity
public class NfvoDomainLayer extends DomainLayer {
    private ManoNbiType manoNbiType;

    public NfvoDomainLayer(){}

    public NfvoDomainLayer(String domainLayerId, ManoNbiType manoNbiType) {
        super(domainLayerId, DomainLayerType.NETWORK_SERVICE_PROVIDER);
        this.manoNbiType=manoNbiType;
    }
}
