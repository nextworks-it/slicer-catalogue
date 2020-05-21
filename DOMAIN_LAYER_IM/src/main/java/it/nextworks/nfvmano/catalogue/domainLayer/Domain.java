package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
public class Domain {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    @Column(unique=true)
    private String domainId;
    private String name;
    private String description;
    private String owner;
    private String admin;

    private DomainStatus domainStatus;


    @Embedded
    private DomainInterface domainInterface;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private List <DomainLayer> ownedLayers=new ArrayList<>();

    //Key: id of domain having an agreement with, value: external domain layers id list the agreement is established with.
    //E.g. If this domain has an agreement with an another domain which ID is C, the key of the map is C and the list will be the C's domain layer to interact to


    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Set<DomainAgreement> domainsAgreement=new HashSet<DomainAgreement>();

    public Domain(){}

    public Domain(String domainId){
        this.domainId=domainId;
    }

    public Domain(String domainId, String name, String description, String owner, String admin, DomainInterface domainInterface){
        this.domainId=domainId;
        this.name=name;
        this.description=description;
        this.owner=owner;
        this.admin=admin;
        this.domainInterface=domainInterface;
        this.domainStatus=DomainStatus.DISABLED;
        this.ownedLayers=new ArrayList<>();
    }

    public Domain(String domainId,
                  String name,
                  String description,
                  String owner,
                  String admin,
                  DomainStatus domainStatus,
                  List <DomainLayer> ownedLayers,
                  Set<DomainAgreement> domainsAgreement,
                  DomainInterface domainInterface){
        this.domainId=domainId;
        this.name=name;
        this.description=description;
        this.owner=owner;
        this.admin=admin;
        this.domainStatus=domainStatus;
        this.ownedLayers=ownedLayers;
        this.domainsAgreement=domainsAgreement;
        this.domainInterface=domainInterface;
    }


    public void isValid() throws MalformattedElementException {
        if (this.domainId == null) {
            throw new MalformattedElementException("Domain Id name not set");
        }else if (this.name == null) {
            throw new MalformattedElementException("Domain name not set");
        }else if (this.owner == null) {
            throw new MalformattedElementException("Domain owner not set");
        } else if (this.admin == null) {
            throw new MalformattedElementException("Domain admin not set");
        } else if (this.domainStatus == null)
            throw new MalformattedElementException("Domain status not set");
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDomainStatus(DomainStatus domainStatus) {
        this.domainStatus = domainStatus;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public List<DomainLayer> getOwnedLayers() {
        return ownedLayers;
    }

    public void setOwnedLayers(List<DomainLayer> ownedLayers) {
        this.ownedLayers = ownedLayers;
    }


    public Set<DomainAgreement> getDomainsAgreement() {
        return domainsAgreement;
    }

    public void setDomainsAgreement(Set<DomainAgreement> domainsAgreement) {
        this.domainsAgreement = domainsAgreement;
    }

    public DomainInterface getDomainInterface() {
        return domainInterface;
    }

    public void setDomainInterface(DomainInterface domainInterface) {
        this.domainInterface = domainInterface;
    }

    public DomainStatus getDomainStatus() {
        return domainStatus;
    }
}
