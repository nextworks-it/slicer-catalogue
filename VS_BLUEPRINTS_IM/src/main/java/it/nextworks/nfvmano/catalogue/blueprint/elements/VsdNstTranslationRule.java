package it.nextworks.nfvmano.catalogue.blueprint.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

public class VsdNstTranslationRule implements InterfaceInformationElement {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<VsdParameterValueRange> input = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String blueprintId;

    private String nstId;

    public VsdNstTranslationRule() { }




    public VsdNstTranslationRule(List<VsdParameterValueRange> input, String nstId, String nsInstantiationLevelId) {
        if (input!= null) this.input = input;

    }


    @Override
    public void isValid() throws MalformattedElementException {

    }

    public Long getId() {
        return id;
    }

    public String getBlueprintId() {
        return blueprintId;
    }

    public void setBlueprintId(String blueprintId) {
        this.blueprintId = blueprintId;
    }

    public String getNstId() {
        return nstId;
    }

    public void setNstId(String nstId) {
        this.nstId = nstId;
    }
}
