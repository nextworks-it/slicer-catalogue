package it.nextworks.nfvmano.catalogue.blueprint.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Site {

    @Id
    @GeneratedValue
    @JsonIgnore
    protected Long id;

    private String siteId;
    private String name;


    public Site(){

    }

    public String getSiteId() {
        return siteId;
    }

    public Site(String siteId, String name){

        this.name=name;
        this.siteId=siteId;
    }




    public String getName() {
        return name;
    }
}
