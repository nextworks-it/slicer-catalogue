package it.nextworks.nfvmano.catalogue.blueprint.elements;


import java.util.Map;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = URLLCServiceParameters.class, name = "urllc"),
        @JsonSubTypes.Type(value = EMBBServiceParameters.class, name = "embb")
})
@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
public abstract class SliceServiceParameters {

    @Id
    @GeneratedValue
    @JsonIgnore
    public Long id;

    @JsonIgnore
    public abstract Map<String, Object> getSliceServiceParameters();
}

