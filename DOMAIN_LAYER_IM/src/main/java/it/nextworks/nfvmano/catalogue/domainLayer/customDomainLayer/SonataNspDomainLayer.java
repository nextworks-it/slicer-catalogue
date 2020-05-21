package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayerType;
import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;

import javax.persistence.Entity;

@Entity
public class SonataNspDomainLayer extends NspDomainLayer {

    private String username;
    private String password;

    public SonataNspDomainLayer(){}

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
}
