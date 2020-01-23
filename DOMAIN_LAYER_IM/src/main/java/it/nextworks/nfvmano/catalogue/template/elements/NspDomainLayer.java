package it.nextworks.nfvmano.catalogue.template.elements;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
public class NspDomainLayer extends DomainLayer {
    private boolean isCSP;

    @OneToOne(cascade = CascadeType.ALL)
    private NfvoDomainLayer nfvoDomainLayer;

    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)

    private List<String> driversList;

    public NspDomainLayer(){}

    public NspDomainLayer(String name, String description, String owner, String admin){
        super(name, description, owner, admin);
    }

    public NspDomainLayer(String name,
                          String description,
                          String owner,
                          String admin,
                          boolean isCSP,
                          List<String> driversList,
                          NfvoDomainLayer nfvoDomainLayer){
        super(name, description, owner, admin);
        this.isCSP=isCSP;
        this.driversList=driversList;
        this.nfvoDomainLayer=nfvoDomainLayer;
    }

    public boolean isCSP() {
        return isCSP;
    }

    public void setCSP(boolean CSP) {
        isCSP = CSP;
    }

    public NfvoDomainLayer getNfvoDomainLayer() {
        return nfvoDomainLayer;
    }

    public void setNfvoDomainLayer(NfvoDomainLayer nfvoDomainLayer) {
        this.nfvoDomainLayer = nfvoDomainLayer;
    }
}
