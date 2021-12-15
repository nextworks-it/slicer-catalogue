package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;

import javax.persistence.Entity;

@Entity
public class SliceManagerNspDomainLayer  extends NspDomainLayer {

    public SliceManagerNspDomainLayer() {
        super(NspNbiType.SLICE_MANAGER);
    }
}
