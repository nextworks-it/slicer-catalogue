package it.nextworks.nfvmano.catalogue.blueprint.elements;

import javax.persistence.Embeddable;

@Embeddable
public class SliceProfile {

    private RadioAccessTechnology radioAccessTechnology;

    private String latency;

    private String uplinkThroughput;

    private String downlinkThroughput;


    public SliceProfile(){}


    public SliceProfile(RadioAccessTechnology radioAccessTechnology, String latency, String uplinkThroughput, String downlinkThroughput) {
        this.radioAccessTechnology = radioAccessTechnology;
        this.latency = latency;
        this.uplinkThroughput = uplinkThroughput;
        this.downlinkThroughput = downlinkThroughput;
    }

    public RadioAccessTechnology getRadioAccessTechnology() {
        return radioAccessTechnology;
    }

    public String getLatency() {
        return latency;
    }

    public String getUplinkThroughput() {
        return uplinkThroughput;
    }

    public String getDownlinkThroughput() {
        return downlinkThroughput;
    }
}
