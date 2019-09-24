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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embeddable;

import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

@Embeddable
@DiscriminatorValue("APPLICATION")
public class ApplicationMetric extends Metric {

	private String topic;
	
	public ApplicationMetric() {
		// JPA only
	}
	
	public ApplicationMetric(String metricId, 
			String name,  
			MetricCollectionType metricCollectionType, 
			String unit, 
			String interval,
			String topic) {
        super(metricId, name, metricCollectionType, unit, interval);
        this.topic = topic;
    }

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}
	
	@Override
    public void isValid() throws MalformattedElementException {
		super.isValid();
		if(topic == null || topic.equals(""))
            throw new MalformattedElementException("Application metric without topic");
	}

}
