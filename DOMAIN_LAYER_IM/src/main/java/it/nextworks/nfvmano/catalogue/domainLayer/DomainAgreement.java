package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
/*
* This class represents the domain agreement.
* The domain id is the id of the domain the agreement is made with.
* domainLayersListConnectedTo is the list of the domains layer id within the domain agreement is made with.
* */
@Entity
public class DomainAgreement {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private String domainAgreeWithId;

    @ElementCollection(fetch=FetchType.EAGER)
    private List<String> domainLayersListAgreeWith;

    public DomainAgreement(){//For JPA only
         }

    public DomainAgreement(String domainAgreeWithId, List<String> domainLayersListAgreeWith){
        this.domainAgreeWithId=domainAgreeWithId;
        this.domainLayersListAgreeWith=domainLayersListAgreeWith;
    }


    public String getDomainAgreeWithId() {
        return domainAgreeWithId;
    }

    public void setDomainAgreeWithId(String domainAgreeWithId) {
        this.domainAgreeWithId = domainAgreeWithId;
    }

    public List<String> getDomainLayersListAgreeWith() {
        return domainLayersListAgreeWith;
    }

    public void setDomainLayersListAgreeWith(List<String> domainLayersListAgreeWith) {
        this.domainLayersListAgreeWith = domainLayersListAgreeWith;
    }
}
