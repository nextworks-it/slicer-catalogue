package it.nextworks.nfvmano.catalogue.template.messages;

public class QueryNsTemplateResourceUsage {

    private String nstId;
    private String nsdId;
    private String nsdDf;
    private String nsInstantiationLevel;

    public QueryNsTemplateResourceUsage(String nstId, String nsdId, String nsdDf, String nsInstantiationLevel) {
        this.nstId = nstId;
        this.nsdId = nsdId;
        this.nsdDf = nsdDf;
        this.nsInstantiationLevel = nsInstantiationLevel;
    }

    public QueryNsTemplateResourceUsage() {
    }

    public String getNstId() {
        return nstId;
    }

    public String getNsdId() {
        return nsdId;
    }

    public String getNsdDf() {
        return nsdDf;
    }

    public String getNsInstantiationLevel() {
        return nsInstantiationLevel;
    }

    public void isValid(){
        //TODO
    }
}
