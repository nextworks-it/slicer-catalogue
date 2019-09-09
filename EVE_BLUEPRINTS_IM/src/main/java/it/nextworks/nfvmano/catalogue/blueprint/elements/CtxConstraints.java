package it.nextworks.nfvmano.catalogue.blueprint.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.nextworks.nfvmano.libs.common.DescriptorInformationElement;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Embeddable
public class CtxConstraints implements DescriptorInformationElement {


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @NotEmpty
    private List<@NotBlank String> compatibleVsBlueprints = new ArrayList<>();

    // 0 means the component should be composed as first or as soon as possible.
    @NotNull
    @PositiveOrZero
    private Integer compositionOrder = 0;

    public CtxConstraints() {
    }

    public CtxConstraints(List<String> compatibleVsBlueprints, Integer compositionOrder) {
        this.compatibleVsBlueprints = compatibleVsBlueprints;
        this.compositionOrder = compositionOrder;
    }

    public List<String> getCompatibleVsBlueprints() {
        return compatibleVsBlueprints;
    }

    public void setCompatibleVsBlueprints(List<String> compatibleVsBlueprints) {
        this.compatibleVsBlueprints = compatibleVsBlueprints;
    }

    public Integer getCompositionOrder() {
        return compositionOrder;
    }

    public void setCompositionOrder(Integer compositionOrder) {
        this.compositionOrder = compositionOrder;
    }

    @Override
    public void isValid() {
    }
}