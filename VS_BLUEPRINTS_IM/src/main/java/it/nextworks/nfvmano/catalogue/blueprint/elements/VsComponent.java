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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import it.nextworks.nfvmano.libs.ifa.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

@Entity
public class VsComponent implements DescriptorInformationElement {

	@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
	
	@JsonIgnore
	@ManyToOne
	private Blueprint vsb;
	//private VsBlueprint vsb;
	
	private String componentId;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private int serversNumber;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> imagesUrls = new ArrayList<>();
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> endPointsIds = new ArrayList<>();
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private Map<String, String> lifecycleOperations = new HashMap<>();


	private VsComponentPlacement placement;

	private VsComponentType type;

	//Reference to the blueprint in the case of type VS
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String associatedVsbId;

	private String compatibleSite;

	public VsComponent() {
		// JPA only
	}
	
	/**
	 * Constructor
	 * 
	 * @param vsb this component belongs to
	 * @param componentId ID of the atomic component
	 * @param serversNumber number of application servers
	 * @param imagesUrls URLs of the images of the application
	 * @param endPointsIds IDs of the connection end points of the applications 
	 * @param lifecycleOperations map with LCM operation as key and script to be executed as value
	 * @param vsComponentType the type of component (VS/VNF at the moment)
	 *  @param placement high-level placement indications for the component, used for the VNFs for the moment
	 */
	public VsComponent(Blueprint vsb,
			String componentId,
			int serversNumber,
			List<String> imagesUrls,
			List<String> endPointsIds,
			Map<String, String> lifecycleOperations,
					   VsComponentType vsComponentType,
					   VsComponentPlacement placement,
					   String associatedVsbId,
					   String compatibleSite) {
		this.vsb = vsb;
		this.componentId = componentId;
		this.serversNumber = serversNumber;
		if (imagesUrls != null) this.imagesUrls = imagesUrls;
		if (endPointsIds != null) this.endPointsIds = endPointsIds;
		if (lifecycleOperations != null) this.lifecycleOperations = lifecycleOperations;
		this.type = vsComponentType;
		this.placement = placement;
		this.associatedVsbId = associatedVsbId;
		this.compatibleSite =compatibleSite;
	}

	public String getAssociatedVsbId() {
		return associatedVsbId;
	}

	/**
	 * @return the vsb
	 */
	public Blueprint getVsb() {
		return vsb;
	}

	public String getCompatibleSite() {
		return compatibleSite;
	}

	/**
	 * @return the componentId
	 */
	public String getComponentId() {
		return componentId;
	}

	/**
	 * @return the serversNumber
	 */
	public int getServersNumber() {
		return serversNumber;
	}

	/**
	 * @return the imagesUrls
	 */
	public List<String> getImagesUrls() {
		return imagesUrls;
	}

	/**
	 * @return the endPointsIds
	 */
	public List<String> getEndPointsIds() {
		return endPointsIds;
	}

	/**
	 * @return the lifecycleOperations
	 */
	public Map<String, String> getLifecycleOperations() {
		return lifecycleOperations;
	}

	@Override
	public void isValid() throws MalformattedElementException {
		if (componentId == null) throw new MalformattedElementException("VSB atomic component without ID.");
		if (this.type==VsComponentType.SERVICE &&  associatedVsbId==null) throw new MalformattedElementException("Component of type service without associated VSB id");
	}

	public VsComponentPlacement getPlacement() {
		return placement;
	}

	public VsComponentType getType() {
		return type;
	}
}
