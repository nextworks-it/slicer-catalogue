package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import it.nextworks.nfvmano.catalogue.domainLayer.ManoNbiType;
import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;

import javax.persistence.Entity;

@Entity
public class OsmNspDomainLayer extends NspDomainLayer {

    private String username;
    private String password;
    private String project;
    private String vimAccount;

    public OsmNspDomainLayer(){}

    public OsmNspDomainLayer(String domainLayerId, String username, String password, String project, String vimAccount) {
        super(domainLayerId, NspNbiType.OSM, false);
        this.username = username;
        this.password = password;
        this.project = project;
        this.vimAccount = vimAccount;
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

    public String getVimAccount() {
        return vimAccount;
    }
}
