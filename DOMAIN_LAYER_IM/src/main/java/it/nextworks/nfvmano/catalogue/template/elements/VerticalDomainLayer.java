package it.nextworks.nfvmano.catalogue.template.elements;

import javax.persistence.*;

@Entity
public class VerticalDomainLayer extends DomainLayer {

    @OneToOne(cascade = CascadeType.ALL)
    private VerticalDomainLayer federatedBy;

    @OneToOne(cascade = CascadeType.ALL)
    private NspDomainLayer nspDomainLayer;

    public VerticalDomainLayer(){    }

    public VerticalDomainLayer(String name,
                               String description,
                               String owner,
                               String admin,
                               VerticalDomainLayer federatedBy,
                               NspDomainLayer nspDomainLayer){
        super(name, description, owner, admin);
        this.federatedBy=federatedBy;
        this.nspDomainLayer=nspDomainLayer;
    }

    public NspDomainLayer getNspDomainLayer() {
        return nspDomainLayer;
    }

    public void setNspDomainLayer(NspDomainLayer nspDomainLayer) {
        this.nspDomainLayer = nspDomainLayer;
    }


    public VerticalDomainLayer getFederatedBy() {
        return federatedBy;
    }

    public void setFederatedBy(VerticalDomainLayer federatedBy) {
        this.federatedBy = federatedBy;
    }
}
