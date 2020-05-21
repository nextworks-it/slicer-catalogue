package it.nextworks.nfvmano.catalogue.domainLayer;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.*;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NfvoDomainLayer.class, name = "Nfvo"),
        @JsonSubTypes.Type(value = NspDomainLayer.class, name = "Nsp"),
        @JsonSubTypes.Type(value = VerticalDomainLayer.class, name = "Vertical")})
public abstract class DomainLayer {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @JsonIgnore
    private Long id;
    @Column(unique=true)
    private String domainLayerId;
    private DomainLayerType domainLayerType;

    public DomainLayer(){}
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
}
