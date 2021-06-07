package it.nextworks.nfvmano.catalogue.arbitrator.messages;


import it.nextworks.nfvmano.catalogue.arbitrator.im.ArbitratorPolicyInfo;

import java.util.List;

public class QueryArbitrationPolicyResponse {


    private List<ArbitratorPolicyInfo> policies;


    public QueryArbitrationPolicyResponse(List<ArbitratorPolicyInfo> policies) {
        this.policies = policies;
    }

    public List<ArbitratorPolicyInfo> getPolicies() {
        return policies;
    }
}
