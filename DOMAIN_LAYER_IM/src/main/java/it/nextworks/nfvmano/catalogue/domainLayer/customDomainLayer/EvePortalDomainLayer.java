package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import it.nextworks.nfvmano.catalogue.domainLayer.DspType;
import it.nextworks.nfvmano.catalogue.domainLayer.VerticalDomainLayer;

import javax.persistence.Entity;
@Entity
public class EvePortalDomainLayer extends VerticalDomainLayer {


    private String rbacUrl;
    private String catalogueUrl;
    private String elcmUrl;
    private String username;
    private String password;

    public EvePortalDomainLayer()
    {

    }
    public EvePortalDomainLayer(String domainId, String rbacUrl, String catalogueUrl, String elcmUrl, String username, String password){
        super(domainId, DspType.EVE_PORTAL);
        this.rbacUrl=rbacUrl;
        this.catalogueUrl=catalogueUrl;
        this.elcmUrl=elcmUrl;
        this.username=username;
        this.password=password;

    }

    public String getRbacUrl() {
        return rbacUrl;
    }

    public String getCatalogueUrl() {
        return catalogueUrl;
    }

    public String getElcmUrl() {
        return elcmUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
