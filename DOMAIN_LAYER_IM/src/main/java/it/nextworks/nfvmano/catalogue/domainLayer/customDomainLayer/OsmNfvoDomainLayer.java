package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayerType;
import it.nextworks.nfvmano.catalogue.domainLayer.ManoNbiType;
import it.nextworks.nfvmano.catalogue.domainLayer.NfvoDomainLayer;

import javax.persistence.Entity;

@Entity
public class OsmNfvoDomainLayer extends NfvoDomainLayer {

    private String username;
    private String password;
    private String project;

    public OsmNfvoDomainLayer(){}

    public OsmNfvoDomainLayer(String domainLayerId, String username, String password, String project) {
        super(domainLayerId, ManoNbiType.OSM_DRIVER);
        this.username = username;
        this.password = password;
        this.project = project;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getProject() {
        return project;
    }
}
