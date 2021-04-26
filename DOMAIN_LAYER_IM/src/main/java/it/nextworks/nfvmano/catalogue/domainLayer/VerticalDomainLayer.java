package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.EvePortalDomainLayer;

import javax.persistence.*;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EvePortalDomainLayer.class, name = "EVE_PORTAL_DSP")})
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
