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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.nextworks.nfvmano.libs.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;

@Entity
public class VsbForwardingPathHop implements DescriptorInformationElement {

	@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
	
	@JsonIgnore
	@ManyToOne
	//private VsBlueprint vsb;
	private Blueprint vsb;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<VsbForwardingPathEndPoint> hopEndPoints = new ArrayList<>();
	
	public VsbForwardingPathHop() {
		// JPA only
	}
	
	/**
	 * Constructor
	 * 
	 * @param vsb Blueprint owing the forwarding path this hop belongs to
	 * @param hopEndPoints list of FP end points that constitute the leaves of the given FP hop 
	 */
	public VsbForwardingPathHop(Blueprint vsb,
			List<VsbForwardingPathEndPoint> hopEndPoints) {
		this.vsb = vsb;
		if (hopEndPoints != null) this.hopEndPoints = hopEndPoints;
	}

	/**
	 * @return the hopEndPoints
	 */
	public List<VsbForwardingPathEndPoint> getHopEndPoints() {
		return hopEndPoints;
	}
	
	@Override
	public void isValid() throws MalformattedElementException {
		if ((hopEndPoints == null) || (hopEndPoints.isEmpty())) throw new MalformattedElementException("VSB Forwarding Path hop without any end point");
		for (VsbForwardingPathEndPoint ep : hopEndPoints) ep.isValid();
	}

}
