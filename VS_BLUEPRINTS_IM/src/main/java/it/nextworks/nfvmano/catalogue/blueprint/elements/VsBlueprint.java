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

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;


@Entity(name = "VsBlueprint")
@DiscriminatorValue("VSB")
public class VsBlueprint extends Blueprint {

//	@Id
//	@GeneratedValue
//	@JsonIgnore
//	private Long id;
//	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<EveSite> compatibleSites = new ArrayList<>();
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> compatibleContextBlueprint = new ArrayList<>();

//	private String vsBlueprintId;
//	private String version;
//	private String name;
//	private String description;
//
//	@JsonInclude(JsonInclude.Include.NON_EMPTY)
//	@ElementCollection(fetch=FetchType.EAGER)
//	@Fetch(FetchMode.SELECT)
//	@Cascade(org.hibernate.annotations.CascadeType.ALL)
//	private List<VsBlueprintParameter> parameters = new ArrayList<>();
//
//	@JsonInclude(JsonInclude.Include.NON_EMPTY)
//	@OneToMany(mappedBy = "vsb", cascade=CascadeType.ALL)
//	@OnDelete(action = OnDeleteAction.CASCADE)
//	@LazyCollection(LazyCollectionOption.FALSE)
//	private List<VsComponent> atomicComponents = new ArrayList<>();
//
//	@JsonInclude(JsonInclude.Include.NON_EMPTY)
//	@OneToMany(mappedBy = "vsb", cascade=CascadeType.ALL)
//	@OnDelete(action = OnDeleteAction.CASCADE)
//	@LazyCollection(LazyCollectionOption.FALSE)
//	private List<VsbForwardingPathHop> serviceSequence = new ArrayList<>();
//
//	@JsonInclude(JsonInclude.Include.NON_EMPTY)
//	@ElementCollection(fetch=FetchType.EAGER)
//	@Fetch(FetchMode.SELECT)
//	@Cascade(org.hibernate.annotations.CascadeType.ALL)
//	private List<VsbEndpoint> endPoints = new ArrayList<>();
//
//	@JsonInclude(JsonInclude.Include.NON_EMPTY)
//	@OneToMany(mappedBy = "vsb", cascade=CascadeType.ALL)
//	@OnDelete(action = OnDeleteAction.CASCADE)
//	@LazyCollection(LazyCollectionOption.FALSE)
//	private List<VsbLink> connectivityServices = new ArrayList<>();
//
//	@JsonInclude(JsonInclude.Include.NON_EMPTY)
//	@ElementCollection(fetch=FetchType.EAGER)
//	@Fetch(FetchMode.SELECT)
//	@Cascade(org.hibernate.annotations.CascadeType.ALL)
//	private List<String> configurableParameters = new ArrayList<>();


	private boolean interSite = false;


	public VsBlueprint() { }

	public VsBlueprint(String vsBlueprinId,
			String version,
			String name,
			String description,
			List<VsBlueprintParameter> parameters,
			List<VsbEndpoint> endPoints,
			List<String> configurableParameters, 
			List<EveSite> compatibleSites,
			List<String> compatibleContextBlueprint, 
			List<ApplicationMetric> applicationMetrics,
					   boolean interSite) {
		super(vsBlueprinId, version, name, description, parameters, endPoints, 
				configurableParameters, applicationMetrics);
		if (compatibleSites != null) this.compatibleSites = compatibleSites;
		if (compatibleContextBlueprint != null) this.compatibleContextBlueprint = compatibleContextBlueprint;
		this.interSite=interSite;
//		this.vsBlueprintId = vsBlueprinId;
//		this.version = version;
//		this.name = name;
//		this.description = description;
//		if (parameters != null) this.parameters = parameters;
//		if (endPoints != null) this.endPoints = endPoints;
//		if (configurableParameters != null) this.configurableParameters = configurableParameters;
	}


//	/**
//	 * @return the vsBlueprintId
//	 */
//	public String getVsBlueprintId() {
//		return vsBlueprintId;
//	}
//
//	/**
//	 * @param vsBlueprintId the vsBlueprintId to set
//	 */
//	public void setVsBlueprintId(String vsBlueprintId) {
//		this.vsBlueprintId = vsBlueprintId;
//	}
//
//
//
//	/**
//	 * @return the version
//	 */
//	public String getVersion() {
//		return version;
//	}
//
//	/**
//	 * @return the id
//	 */
//	public Long getId() {
//		return id;
//	}
//
//	/**
//	 * @return the name
//	 */
//	public String getName() {
//		return name;
//	}
//
//	/**
//	 * @return the description
//	 */
//	public String getDescription() {
//		return description;
//	}
//
//	/**
//	 * @return the parameters
//	 */
//	public List<VsBlueprintParameter> getParameters() {
//		return parameters;
//	}
//
//	/**
//	 * @return the atomicComponents
//	 */
//	public List<VsComponent> getAtomicComponents() {
//		return atomicComponents;
//	}
//
//	/**
//	 * @return the serviceSequence
//	 */
//	public List<VsbForwardingPathHop> getServiceSequence() {
//		return serviceSequence;
//	}
//
//	/**
//	 * @return the endPoints
//	 */
//	public List<VsbEndpoint> getEndPoints() {
//		return endPoints;
//	}
//
//
//
//	/**
//	 * @return the connectivityServices
//	 */
//	public List<VsbLink> getConnectivityServices() {
//		return connectivityServices;
//	}
//
//
//
//	/**
//	 * @return the configurableParameters
//	 */
//	public List<String> getConfigurableParameters() {
//		return configurableParameters;
//	}


	public boolean isInterSite() {
		return interSite;
	}

	/**
	 * 
	 * @return the connection point towards the RAN segment
	 */
	@JsonIgnore
	public String getRanEndPoint() {
		for (VsbEndpoint e : endPoints) {
			if (e.isRanConnection()) return e.getEndPointId();
		}
		return null;
	}

	/**
	 * @return the compatibleSites
	 */
	public List<EveSite> getCompatibleSites() {
		return compatibleSites;
	}

	/**
	 * @return the compatibleContextBlueprint
	 */
	public List<String> getCompatibleContextBlueprint() {
		return compatibleContextBlueprint;
	}

	@Override
	public void isValid() throws MalformattedElementException {
		super.isValid();
		if (endPoints == null || endPoints.isEmpty()) {
			if(!isInterSite())
				throw new MalformattedElementException("Blueprint without end points");
		} else {
			for (VsbEndpoint e : endPoints) {
				e.isValid();
			}
		}
		if (compatibleSites == null || compatibleSites.isEmpty()) {
			throw new MalformattedElementException("VS Blueprint without compatible sites");
		}
	}

	//SHOULD ONLY BE USED FOR TESTING PURPOSES
//	public void setId(Long id) {
//		this.id = id;
//	}

}
