package it.nextworks.nfvmano.catalogue.template.elements;

import javax.persistence.Entity;

@Entity
public class NfvoDomainLayer extends DomainLayer {
    private boolean isRanEnabled;

    public NfvoDomainLayer(){}

    public NfvoDomainLayer(String name,
                           String description,
                           String owner,
                           String admin,
                           boolean isRanEnabled){
        super( name, description, owner, admin);
        this.isRanEnabled=isRanEnabled;
    }

    public boolean isRanEnabled() {
        return isRanEnabled;
    }

    public void setRanEnabled(boolean ranEnabled) {
        isRanEnabled = ranEnabled;
    }

}
