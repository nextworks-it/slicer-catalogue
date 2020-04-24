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


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.nextworks.nfvmano.libs.ifa.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

@Entity
public class TestCaseBlueprint implements DescriptorInformationElement{

	@Id
    @GeneratedValue
    @JsonIgnore
    protected Long id;
	
	protected String version;
    protected String name;
    protected String description;
	
	private String testcaseBlueprintId;
	
	//TODO: to be discussed if this is the script or the script ID
	@Lob @Basic(fetch=FetchType.EAGER)
	@Column(columnDefinition = "TEXT")
	private String script;
	
	//parameters to be passed by the experimenter using the descriptor and added automatically in the script
	//key: name of the parameter, as shown to the experimenter when filling the descriptor
	//value: name of the variable in the script
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private Map<String, String> userParameters = new HashMap<String, String>();

	//parameters to be retrieved at runtime by the system and added automatically in the script after the instantiation
	//key: expression to identify uniquely the parameter. The format could be as follows: ns.<nsd_id>.vnf.<vnf_id>.cp.<cpd_id>.ip_addr
	//value: name of the variable in the script
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private Map<String, String> infrastructureParameters = new HashMap<String, String>();
	
	public TestCaseBlueprint() {
		// JPA only
	}
	
	public TestCaseBlueprint(String testcaseBlueprintId,
			String name, 
			String version, 
			String description,
			String script,
			Map<String, String> userParameters,
			Map<String, String> infrastructureParameters) {
		this.testcaseBlueprintId = testcaseBlueprintId;
		this.name = name;
		this.version = version;
		this.description = description;
		this.script = script;
		if (userParameters != null) this.userParameters = userParameters;
		if (infrastructureParameters != null) this.infrastructureParameters = infrastructureParameters;
	}
	
	

	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the testcaseBlueprintId
	 */
	public String getTestcaseBlueprintId() {
		return testcaseBlueprintId;
	}
	
	

	/**
	 * @param testcaseBlueprintId the testcaseBlueprintId to set
	 */
	public void setTestcaseBlueprintId(String testcaseBlueprintId) {
		this.testcaseBlueprintId = testcaseBlueprintId;
	}

	/**
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * @return the userParameters
	 */
	public Map<String, String> getUserParameters() {
		return userParameters;
	}

	/**
	 * @return the infrastructureParameters
	 */
	public Map<String, String> getInfrastructureParameters() {
		return infrastructureParameters;
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
	
	@JsonIgnore
	public boolean isCompatibleWithDescriptorParameters(Set<String> descriptorParameters) {
		boolean foundInBlueprint = false;
		Set<String> userParameterNames = userParameters.keySet();
		for (String s : descriptorParameters) {
			foundInBlueprint = false;
			for (String userParameterName : userParameterNames) {
				if (s.equals(userParameterName)) foundInBlueprint = true;
			}
			//the descriptor parameters is not found in the blueprint
			if (foundInBlueprint == false) return false;
		}
		return true;
	}

	@Override
    public void isValid() throws MalformattedElementException {
		if (testcaseBlueprintId == null || testcaseBlueprintId.isEmpty())
			throw new MalformattedElementException("Test case blueprint without id");
		if (name == null || name.isEmpty())
			throw new MalformattedElementException("Test case blueprint without name");
		if (version == null || version.isEmpty())
			throw new MalformattedElementException("Test case blueprint without version");
		if (script == null || script.isEmpty())
			throw new MalformattedElementException("Test case blueprint without script");
	}
}
