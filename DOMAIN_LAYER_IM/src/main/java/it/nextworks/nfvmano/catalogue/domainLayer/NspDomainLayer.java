package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.NHNspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.OsmNspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.SonataNspDomainLayer;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NHNspDomainLayer.class, name = "NH"),
        @JsonSubTypes.Type(value = OsmNspDomainLayer.class, name = "OSM"),
        @JsonSubTypes.Type(value = SonataNspDomainLayer.class, name = "Sonata")})
public class NspDomainLayer extends DomainLayer {
    private NspNbiType nspNbiType;
    private boolean isRANEnabled;

    public NspDomainLayer(){}
    public NspDomainLayer(String domainLayerId, NspNbiType nspNbiType, boolean isRANEnabled) {
        super(domainLayerId, DomainLayerType.NETWORK_SLICE_PROVIDER);
        this.nspNbiType=nspNbiType;
        this.isRANEnabled=isRANEnabled;
    }

    public NspNbiType getNspNbiType() {
        return nspNbiType;
    }

    public boolean isRANEnabled() {
        return isRANEnabled;
    }
}
