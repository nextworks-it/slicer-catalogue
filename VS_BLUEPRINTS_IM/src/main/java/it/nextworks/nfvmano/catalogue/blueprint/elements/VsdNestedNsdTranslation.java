package it.nextworks.nfvmano.catalogue.blueprint.elements;

import javax.persistence.Embeddable;

@Embeddable
public class VsdNestedNsdTranslation {


    private String nsdId;
    private String nsDf;
    private String nsIl;

    public VsdNestedNsdTranslation(String nsdId, String nsDf, String nsIl) {
        this.nsdId = nsdId;
        this.nsDf = nsDf;
        this.nsIl = nsIl;
    }

    public String getNsdId() {
        return nsdId;
    }

    public String getNsDf() {
        return nsDf;
    }

    public String getNsIl() {
        return nsIl;
    }
}
