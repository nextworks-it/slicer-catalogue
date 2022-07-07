package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.*;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NHNspDomainLayer.class, name = "NEUTRAL_HOSTING_NSP"),
        @JsonSubTypes.Type(value = OsmNspDomainLayer.class, name = "OSM_NSP"),
        @JsonSubTypes.Type(value = SonataNspDomainLayer.class, name = "SONATA_NSP"),
        @JsonSubTypes.Type(value = SliceManagerNspDomainLayer.class, name = "SLICE_MANAGER_NSP"),
        @JsonSubTypes.Type(value = SliceManagerFsmNspDomainLayer.class, name = "SLICE_MANAGER_FSM_NSP"),
        @JsonSubTypes.Type(value = SebastianNspDomainLayer.class, name = "SEBASTIAN_NSP")
})
public class NspDomainLayer extends DomainLayer {
    @JsonProperty("nspNbiType")
    private NspNbiType nspNbiType;
   
    @JsonProperty("ranEnabled")
    private boolean ranEnabled;

    public NspDomainLayer(){
        super(DomainLayerType.NETWORK_SLICE_PROVIDER);
    }

    public NspDomainLayer(NspNbiType nspNbiType){
        super(DomainLayerType.NETWORK_SLICE_PROVIDER);
        this.nspNbiType = nspNbiType;
    }

    public NspDomainLayer(String domainLayerId, NspNbiType nspNbiType, boolean isRANEnabled) {
        super(domainLayerId, DomainLayerType.NETWORK_SLICE_PROVIDER);
        this.nspNbiType=nspNbiType;
        this.ranEnabled=isRANEnabled;
    }

    public NspNbiType getNspNbiType() {
        return nspNbiType;
    }

    public void setNspNbiType(NspNbiType nspNbiType) {
        this.nspNbiType = nspNbiType;
    }

    public boolean isRanEnabled() {
        return ranEnabled;
    }

    public void setRanEnabled(boolean ranEnabled) {
        this.ranEnabled = ranEnabled;
    }
}
