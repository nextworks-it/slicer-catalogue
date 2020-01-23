package it.nextworks.nfvmano.catalogue.template.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the info about a domain layer. A domain layer can be considered kind of a node in the network.
 * It can be logically connected to other nodes and composing with them a graph. The inherited classes from this one,
 * specify what kind of domain layer is referring to, with specific information.

 * @param  id  the id of the domain layer.
 * @param  name the name of domain layer.
 * @param  description  the description of the domain layer.
 * @param  owner the owner of domain layer.
 * @param  admin the admin of domain layer.
 * @param  domainLayerStatus the ids list of domain layer connected to.
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class DomainLayer {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    private String name;
    private String description;
    private String owner;
    private String admin;

    private DomainLayerStatus domainLayerStatus=DomainLayerStatus.DISABLED;

    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<InterfaceType> interfaceTypeList=new ArrayList<InterfaceType>();

    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> domainLayersIdAssociated = new ArrayList<Long>();

    public DomainLayer(){}

    public DomainLayer(String name, String description, String owner, String admin){
        this.name=name;
        this.description=description;
        this.owner=owner;
        this.admin=admin;
        this.interfaceTypeList=new ArrayList<InterfaceType>();
        this.domainLayersIdAssociated=new ArrayList<Long>();
    }

    public DomainLayer(String name,
                       String description,
                       String owner,
                       String admin,
                       List<InterfaceType> interfaceTypeList,
                       List<Long> domainLayersIdAssociated){
        this.name=name;
        this.description=description;
        this.owner=owner;
        this.admin=admin;
        this.interfaceTypeList=interfaceTypeList;
        this.domainLayersIdAssociated=domainLayersIdAssociated;
    }

    @ElementCollection(targetClass=InterfaceType.class)
    public List<InterfaceType> getInterfaceTypeList() {
        return interfaceTypeList;
    }

    public void setInterfaceTypeList(List<InterfaceType> interfaceTypeList) {
        this.interfaceTypeList = interfaceTypeList;
    }

    public void isValid() throws MalformattedElementException {
        if (this.name == null) {
            throw new MalformattedElementException("Domain Layer name not set");
        }else if (this.description == null) {
            throw new MalformattedElementException("Domain Layer description not set");
        }else if (this.owner == null) {
            throw new MalformattedElementException("Domain Layer owner not set");
        } else if (this.admin == null) {
            throw new MalformattedElementException("Domain Layer admin not set");
        }
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

    public List<Long> getDomainLayersIdAssociated() {
        return domainLayersIdAssociated;
    }

    public void setDomainLayersIdAssociated(List<Long> domainLayersIdAssociated){
        this.domainLayersIdAssociated=domainLayersIdAssociated;
    }

    public void setDomainLayerStatus(DomainLayerStatus domainLayerStatus) {
        this.domainLayerStatus = domainLayerStatus;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public DomainLayerStatus getDomainLayerStatus() {
        return domainLayerStatus;
    }


}
