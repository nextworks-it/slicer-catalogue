package it.nextworks.nfvmano.catalogue.arbitrator.im;


import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@Entity
public class ArbitratorPolicy {

    @Id
    @GeneratedValue
    @JsonIgnore
    public Long id;

    private String arbitratorPolicyId;

    private String name;
    private PolicyUpdateStrategy policyUpdateStrategy;

    @JsonIgnore
    @OneToOne(fetch=FetchType.EAGER, mappedBy = "arbitratorPolicy", cascade=CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ArbitratorPolicyInfo arbitratorPolicyInfo;

    @OneToOne(fetch=FetchType.EAGER, mappedBy = "policy", cascade=CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ArbitratorPolicyFilter filter;

    private String modelId;
    private boolean isDefault=false;


    public ArbitratorPolicyInfo getArbitratorPolicyInfo() {
        return arbitratorPolicyInfo;
    }




    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<String, String> policyUpdateStrategyParams = new HashMap<>();

    public ArbitratorPolicy() {
    }

    public ArbitratorPolicy(String arbitratorPolicyUpdateId, String name, PolicyUpdateStrategy policyUpdateStrategy, ArbitratorPolicyFilter filter, String modelId, Map<String, String> policyUpdateStrategyParams, boolean isDefault) {
        this.arbitratorPolicyId = arbitratorPolicyUpdateId;
        this.policyUpdateStrategy = policyUpdateStrategy;
        this.filter = filter;
        this.modelId = modelId;
        this.name= name;
        if(policyUpdateStrategyParams!=null) this.policyUpdateStrategyParams = policyUpdateStrategyParams;
        this.isDefault = isDefault;
    }

    public void setArbitratorPolicyId(String arbitratorPolicyId) {
        this.arbitratorPolicyId = arbitratorPolicyId;
    }

    public String getArbitratorPolicyId() {
        return arbitratorPolicyId;
    }

    public PolicyUpdateStrategy getPolicyUpdateStrategy() {
        return policyUpdateStrategy;
    }

    public ArbitratorPolicyFilter getFilter() {
        return filter;
    }

    public void setFilter(ArbitratorPolicyFilter filter) {
        this.filter = filter;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModelId() {
        return modelId;
    }


    public Map<String, String> getPolicyUpdateStrategyParams() {
        return policyUpdateStrategyParams;
        //return new HashMap<>();
    }



    public boolean isDefault() {
        return isDefault;
    }
}
