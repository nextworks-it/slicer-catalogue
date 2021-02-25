package it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayerType;
import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;

import javax.persistence.Entity;

@Entity
public class NHNspDomainLayer extends NspDomainLayer {

    @JsonProperty("userId")
    private String userId;
    @JsonProperty("tenantId")
    private String tenantId;//sliceId

    public NHNspDomainLayer(){
        super(NspNbiType.NEUTRAL_HOSTING);
    }

    public NHNspDomainLayer(String domainLayerId, String userId, String tenantId) {
        super(domainLayerId, NspNbiType.NEUTRAL_HOSTING, true);
        this.userId = userId;
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
