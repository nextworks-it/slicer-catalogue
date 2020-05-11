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


import javax.persistence.Embeddable;

import it.nextworks.nfvmano.libs.ifa.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

@Embeddable
public class ApplicationMetric implements DescriptorInformationElement {

	private String topic;
	private String metricId;
    private String name;
    private MetricCollectionType metricCollectionType;
    private MetricGraphType metricGraphType;
    private String unit;
    private String interval;
	
	public ApplicationMetric() {
		// JPA only
	}
	
	public ApplicationMetric(String metricId, 
			String name,  
			MetricCollectionType metricCollectionType, 
			String unit, 
			String interval,
			String topic,
                             MetricGraphType metricGraphType) {
        //super(metricId, name, metricCollectionType, unit, interval);
		this.metricId = metricId;
        this.name = name;
        this.metricCollectionType = metricCollectionType;
        this.unit = unit;
        this.interval = interval;
        this.topic = topic;
        this.metricGraphType = metricGraphType;
    }


    public MetricGraphType getMetricGraphType() {
        return metricGraphType;
    }

    public void setMetricGraphType(MetricGraphType metricGraphType) {
        this.metricGraphType = metricGraphType;
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

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	@Override
    public void isValid() throws MalformattedElementException {
		if(metricId == null || metricId.isEmpty())
            throw new MalformattedElementException("Metric without metricId");
        if(name == null || name.isEmpty())
            throw new MalformattedElementException("Metric without name");
        if(unit == null || unit.isEmpty())
            throw new MalformattedElementException("Metric without unit");
        if(metricCollectionType == null)
            throw new MalformattedElementException("Metric without MetricCollectionType");
        if(metricGraphType == null)
            throw new MalformattedElementException("Metric without MetricGraphType");
	}
}
