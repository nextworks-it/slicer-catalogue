package it.nextworks.nfvmano.catalogue.blueprint.elements;

import it.nextworks.nfvmano.libs.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;

import javax.persistence.Embeddable;

@Embeddable
public class Metric implements DescriptorInformationElement {


    private String metricId;


    private String name;


    private MetricType metricType;


    private MetricCollectionType metricCollectionType;


    private String unit;


    private String interval;

    /*// Additional data for this metric.
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<@NotBlank String, @NotBlank String> metadata = new HashMap<>();*/

    public Metric() {
    }

    public Metric(String metricId, String name, MetricType metricType, MetricCollectionType metricCollectionType, String unit, String interval) {
        this.metricId = metricId;
        this.name = name;
        this.metricType = metricType;
        this.metricCollectionType = metricCollectionType;
        this.unit = unit;
        this.interval = interval;
    }

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MetricCollectionType getMetricCollectionType() {
        return metricCollectionType;
    }

    public void setMetricCollectionType(MetricCollectionType metricCollectionType) {
        this.metricCollectionType = metricCollectionType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    //public Map<String, String> getMetadata() {
     //   return metadata;
    //}

    //public void setMetadata(Map<String, String> metadata) {
    //    this.metadata = metadata;
    //}

    public MetricType getMetricType() {
        return metricType;
    }

    @Override
    public void isValid() throws MalformattedElementException {

        if(metricId == null || metricId.equals(""))
            throw new MalformattedElementException("Metric without metricId");
        if(name == null || name.equals(""))
            throw new MalformattedElementException("Metric without name");
        if(unit == null || unit.equals(""))
            throw new MalformattedElementException("Metric without unit");
        if(metricCollectionType == null)
            throw new MalformattedElementException("Metric without MetricCollectionType");
        if(metricType == null)
            throw new MalformattedElementException("Metric without MetricType");
        if(interval == null || interval.equals(""))
            throw new MalformattedElementException("Metric without interval");


    }

    enum MetricCollectionType {
        CUMULATIVE,
        DELTA,
        GAUGE
    }

    enum MetricType {
        LOST_PKT,
        RECEIVED_PKT,
        SENT_PKT,
        BANDWIDTH,
        LATENCY,
        JITTER,
        CPU_CONSUMPTION,
        MEMORY_CONSUMPTION,
        OTHER
    }
}