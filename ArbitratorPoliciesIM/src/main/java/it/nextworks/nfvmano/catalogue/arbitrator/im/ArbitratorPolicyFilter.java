package it.nextworks.nfvmano.catalogue.arbitrator.im;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
public class ArbitratorPolicyFilter {

    @Id
    @GeneratedValue
    @JsonIgnore
    public Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<ArbitratorPolicySelector, String> filterData = new HashMap<>();


    public ArbitratorPolicyFilter() {
    }

    public ArbitratorPolicy getPolicy() {
        return policy;
    }

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
    @JsonIgnore
    @JoinColumn(name="arbitrator_policy_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ArbitratorPolicy policy;

    public ArbitratorPolicyFilter(ArbitratorPolicy policy, Map<ArbitratorPolicySelector, String> filterData) {
        this.filterData = filterData;
    }

    public Map<ArbitratorPolicySelector, String> getFilterData() {
        return filterData;
    }
}
