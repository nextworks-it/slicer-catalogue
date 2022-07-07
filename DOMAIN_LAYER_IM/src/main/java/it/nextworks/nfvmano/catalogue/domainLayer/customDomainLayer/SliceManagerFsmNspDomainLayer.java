package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;

import javax.persistence.Entity;

@Entity
public class SliceManagerFsmNspDomainLayer extends NspDomainLayer {

    public SliceManagerFsmNspDomainLayer() {
        super(NspNbiType.SLICE_MANAGER_FSM);
    }
}
