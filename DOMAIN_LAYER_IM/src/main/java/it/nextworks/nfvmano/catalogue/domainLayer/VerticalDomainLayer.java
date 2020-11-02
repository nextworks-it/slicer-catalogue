package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
public class VerticalDomainLayer extends DomainLayer {
    @JsonProperty("dspType")
    private DspType dspType;

    public VerticalDomainLayer(){
        super(DomainLayerType.VERTICAL_SERVICE_PROVIDER);
    }

    public VerticalDomainLayer(String domainLayerId, DspType dspType) {
        super(domainLayerId, DomainLayerType.VERTICAL_SERVICE_PROVIDER);
        this.dspType=dspType;
    }
}
