package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;

import javax.persistence.Entity;

@Entity
public class SebastianNspDomainLayer extends NspDomainLayer {

    public SebastianNspDomainLayer() {
        super(NspNbiType.SEBASTIAN);
    }
}
