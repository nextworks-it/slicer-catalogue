package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.catalogue.domainLayer.ManoNbiType;
import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.Entity;

@Entity
public class OsmNspDomainLayer extends NspDomainLayer {

    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("project")
    private String project;
    @JsonProperty("vimAccount")
    private String vimAccount;

    public OsmNspDomainLayer(){
        super(NspNbiType.OSM);
    }

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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public void setVimAccount(String vimAccount) {
        this.vimAccount = vimAccount;
    }
}
