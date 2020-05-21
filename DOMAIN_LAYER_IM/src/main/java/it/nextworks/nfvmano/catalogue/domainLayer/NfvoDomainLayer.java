package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.NHNspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.OsmNfvoDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.SonataNspDomainLayer;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OsmNfvoDomainLayer.class, name = "Osm")})
public class NfvoDomainLayer extends DomainLayer {
    private ManoNbiType manoNbiType;

    public NfvoDomainLayer(){}

    public NfvoDomainLayer(String domainLayerId, ManoNbiType manoNbiType) {
        super(domainLayerId, DomainLayerType.NETWORK_SERVICE_PROVIDER);
        this.manoNbiType=manoNbiType;
    }

    public ManoNbiType getManoNbiType() {
        return manoNbiType;
    }
}
