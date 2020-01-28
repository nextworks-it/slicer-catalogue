package it.nextworks.nfvmano.catalogue.domainLayer;

import javax.persistence.*;

@Entity
public class VerticalDomainLayer extends DomainLayer {
    private DspType dspType;

    public VerticalDomainLayer(){}
    public VerticalDomainLayer(String domainLayerId, DspType dspType) {
        super(domainLayerId, DomainLayerType.VERTICAL_SERVICE_PROVIDER);
        this.dspType=dspType;
    }
}
