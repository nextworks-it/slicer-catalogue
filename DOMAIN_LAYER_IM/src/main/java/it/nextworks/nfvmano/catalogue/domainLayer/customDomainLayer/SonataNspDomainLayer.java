package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayerType;
import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;

import javax.persistence.Entity;

@Entity
public class SonataNspDomainLayer extends NspDomainLayer {

    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

    public SonataNspDomainLayer(){
        super(NspNbiType.SONATA);
    }

    public SonataNspDomainLayer(String domainLayerId, String username, String password) {
        super(domainLayerId, NspNbiType.SONATA, false);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
