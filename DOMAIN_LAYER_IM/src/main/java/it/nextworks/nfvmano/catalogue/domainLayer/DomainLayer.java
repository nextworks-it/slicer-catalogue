package it.nextworks.nfvmano.catalogue.domainLayer;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import javax.persistence.*;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NfvoDomainLayer.class, name = "NFVO"),
        @JsonSubTypes.Type(value = NspDomainLayer.class, name = "NSP"),
        @JsonSubTypes.Type(value = VerticalDomainLayer.class, name = "VERTICAL")})
public abstract class DomainLayer {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @JsonIgnore
    private Long id;
    @Column(unique=true)
    @JsonProperty("domainLayerId")
    private String domainLayerId;
    @JsonProperty("domainLayerType")
    private DomainLayerType domainLayerType;

    public DomainLayer(){}

    public DomainLayer(DomainLayerType domainLayerType){
        this.domainLayerType = domainLayerType;
    }

    public DomainLayer(String domainLayerId, DomainLayerType domainLayerType){
        this.domainLayerId=domainLayerId;
        this.domainLayerType=domainLayerType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDomainLayerType(DomainLayerType domainLayerType) {
        this.domainLayerType = domainLayerType;
    }

    public String getDomainLayerId() {
        return domainLayerId;
    }

    public void setDomainLayerId(String domainLayerId) {
        this.domainLayerId = domainLayerId;
    }

    public DomainLayerType getDomainLayerType() {
        return domainLayerType;
    }

    public void isValid() throws MalformattedElementException {
        if (this.domainLayerId == null) {
            throw new MalformattedElementException("Domain layer id not set");
        } else if (this.domainLayerType == null) {
            throw new MalformattedElementException("Domain layer type not set");
        }
    }
}
