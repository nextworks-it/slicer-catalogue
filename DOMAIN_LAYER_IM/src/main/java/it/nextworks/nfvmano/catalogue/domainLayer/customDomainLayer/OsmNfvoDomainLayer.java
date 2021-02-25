package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.catalogue.domainLayer.*;

import javax.persistence.Entity;

@Entity
public class OsmNfvoDomainLayer extends NfvoDomainLayer {

    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("project")
    private String project;

    public OsmNfvoDomainLayer(){
        super(ManoNbiType.OSM_DRIVER);
    }

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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
