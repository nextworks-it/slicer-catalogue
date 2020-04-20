package it.nextworks.nfvmano.catalogue.blueprint.elements;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class ExpDescriptorInfo implements InterfaceInformationElement {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    private String expDescriptorId;
    private String name;
    private String expDescriptorVersion;


    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> activeExperimentIds= new ArrayList<>();


    @Transient
    private ExpDescriptor expDescriptor;


    public List<String> getActiveExperimentIds() {
        return activeExperimentIds;
    }

    public void setActiveExperimentIds(List<String> activeExperimentIds) {
        this.activeExperimentIds = activeExperimentIds;
    }

    public ExpDescriptorInfo(){

    }


    public ExpDescriptorInfo(String expDescriptorId, String name, String expDescriptorVersion){
        this.expDescriptorId=expDescriptorId;
        this.name=name;
        this.expDescriptorVersion = expDescriptorVersion;

    }

    public ExpDescriptor getExpDescriptor() {
        return expDescriptor;
    }

    public void setExpDescriptor(ExpDescriptor expDescriptor) {
        this.expDescriptor = expDescriptor;
    }

    public String getExpDescriptorId() {
        return expDescriptorId;
    }

    public String getName() {
        return name;
    }

    public String getExpDescriptorVersion() {
        return expDescriptorVersion;
    }


    @Override
    public void isValid() throws MalformattedElementException {
        if (expDescriptorId == null) throw new MalformattedElementException("EXPD info without EXPD ID");
        if (expDescriptorVersion == null) throw new MalformattedElementException("EXPD info without EXPD expDescriptorVersion");
        if (name == null) throw new MalformattedElementException("EXPD info without ExpD name");
    }
}
