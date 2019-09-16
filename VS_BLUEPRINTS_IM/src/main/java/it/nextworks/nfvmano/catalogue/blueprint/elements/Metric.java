/*
 * Copyright 2018 Nextworks s.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.nextworks.nfvmano.catalogue.blueprint.elements;

import it.nextworks.nfvmano.libs.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embeddable;
import javax.persistence.Inheritance;

@Embeddable
@Inheritance
@DiscriminatorColumn(name="METRIC_T")
public class Metric implements DescriptorInformationElement {


    private String metricId;


    private String name;


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

    public Metric(String metricId, String name, MetricCollectionType metricCollectionType, String unit, String interval) {
        this.metricId = metricId;
        this.name = name;
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

    }

    enum MetricCollectionType {
        CUMULATIVE,
        DELTA,
        GAUGE
    }

}