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

import com.fasterxml.jackson.annotation.JsonIgnore;

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


import com.fasterxml.jackson.annotation.JsonInclude;

import it.nextworks.nfvmano.libs.ifa.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.validation.Valid;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Inheritance
@DiscriminatorColumn(name="BLUEPRINT_TYPE")
public class Blueprint implements DescriptorInformationElement {

	@Id
	@GeneratedValue
	@JsonIgnore
	public Long id;

	protected String blueprintId;
    protected String version;
    protected String name;
    protected String description;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    protected List<VsBlueprintParameter> parameters = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "vsb", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    protected List<VsComponent> atomicComponents = new ArrayList<>();
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
	@OneToMany(mappedBy = "vsb", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	protected List<VsbForwardingPathHop> serviceSequence = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    protected List<VsbEndpoint> endPoints = new ArrayList<>();
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
	@OneToMany(mappedBy = "vsb", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
    protected List<VsbLink> connectivityServices = new ArrayList<>();

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	protected List<String> configurableParameters = new ArrayList<>();
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	protected List<ApplicationMetric> applicationMetrics = new ArrayList<>();

	private boolean interSite = false;

    public Blueprint() { }

    public Blueprint(String blueprintId,
			String version,
			String name,
			String description,
			List<VsBlueprintParameter> parameters,
			List<VsbEndpoint> endPoints,
			List<String> configurableParameters,
			List<ApplicationMetric> applicationMetrics,
					 boolean interSite) {
    	this.blueprintId = blueprintId;
		this.version = version;
		this.name = name;
		this.description = description;
		this.interSite = interSite;
		if (parameters != null) this.parameters = parameters;
		if (endPoints != null) this.endPoints = endPoints;
		if (configurableParameters != null) this.configurableParameters = configurableParameters;
		if (applicationMetrics != null) this.applicationMetrics = applicationMetrics;
    }

	public boolean isInterSite() {
		return interSite;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the blueprintId
	 */
	public String getBlueprintId() {
		return blueprintId;
	}

	/**
	 * @param blueprintId the blueprintId to set
	 */
	public void setBlueprintId(String blueprintId) {
		this.blueprintId = blueprintId;
	}

	/**
	 * @return the serviceSequence
	 */
	public List<VsbForwardingPathHop> getServiceSequence() {
		return serviceSequence;
	}

	/**
	 * @return the connectivityServices
	 */
	public List<VsbLink> getConnectivityServices() {
		return connectivityServices;
	}

	/**
	 * @return the configurableParameters
	 */
	public List<String> getConfigurableParameters() {
		return configurableParameters;
	}

	

    /**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the parameters
	 */
	public List<VsBlueprintParameter> getParameters() {
		return parameters;
	}

	/**
	 * @return the atomicComponents
	 */
	public List<VsComponent> getAtomicComponents() {
		return atomicComponents;
	}

	/**
	 * @return the endPoints
	 */
	public List<VsbEndpoint> getEndPoints() {
		return endPoints;
	}
	
	

	/**
	 * @return the applicationMetrics
	 */
	public List<ApplicationMetric> getApplicationMetrics() {
		return applicationMetrics;
	}

	@JsonIgnore
	public boolean isCompatibleWithDescriptorParameters(Set<String> vsdParameters) {
		boolean foundInBlueprint = false;
		for (String s : vsdParameters) {
			foundInBlueprint = false;
			for (VsBlueprintParameter vsbp : parameters) {
				String vsbParamId = vsbp.getParameterId();
				if (s.equals(vsbParamId)) foundInBlueprint = true;
			}
			//the vsd parameters is not found in the blueprint
			if (foundInBlueprint == false) return false;
		}
		return true;
	}
	
	@Override
    public void isValid() throws MalformattedElementException {
        for (VsBlueprintParameter p : parameters) {
            p.isValid();
        }
        if (version == null) {
            throw new MalformattedElementException("Blueprint without version");
        }
        if (name == null) {
            throw new MalformattedElementException("Blueprint without name");
        }
        if (atomicComponents != null) {
            for (VsComponent c : atomicComponents) {
                c.isValid();
            }
        }
        if (endPoints != null) {
            for (VsbEndpoint e : endPoints) {
                e.isValid();
            }
        }
        if (serviceSequence != null) {
			for (VsbForwardingPathHop e : serviceSequence) e.isValid();
		}
        if (connectivityServices != null) {
			for (VsbLink l : connectivityServices) l.isValid();
		}
        if (applicationMetrics != null) {
        	for (ApplicationMetric am : applicationMetrics) am.isValid();
        }
    }
}
