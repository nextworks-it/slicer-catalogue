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

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.nextworks.nfvmano.libs.ifa.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

@Entity
public class TestCaseDescriptor implements DescriptorInformationElement {

	@Id
    @GeneratedValue
    @JsonIgnore
    protected Long id;
	
	private String testCaseDescriptorId;
	
	private String name;
	private String version;
	private String testCaseBlueprintId;

	//Key: parameter name, as in the key of the corresponding map in the blueprint; value: desired value
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private Map<String, String> userParameters = new HashMap<String, String>();
	
	@JsonIgnore
	private boolean isPublic;

	@JsonIgnore
	private String tenantId;
	
	public TestCaseDescriptor() {	}
	
	/**
	 * Constructor
	 * 
	 * @param testCaseDescriptorId ID of the test case descriptor
	 * @param name name of the test case descriptor
	 * @param version version of the test case descriptor
	 * @param testCaseBlueprintId ID of the test case blueprint associated to the descriptor
	 * @param userParameters values of the user parameters defined in the blueprint
	 */
	public TestCaseDescriptor(String testCaseDescriptorId,
			String name,
			String version,
			String testCaseBlueprintId,
			Map<String, String> userParameters,
			boolean isPublic, 
			String tenantId) {	
		this.testCaseDescriptorId = testCaseDescriptorId;
		this.name = name;
		this.version = version;
		this.testCaseBlueprintId = testCaseBlueprintId;
		if (userParameters != null) this.userParameters = userParameters;
		this.isPublic = isPublic;
		this.tenantId = tenantId;
	}

	/**
	 * @return the testCaseDescriptorId
	 */
	public String getTestCaseDescriptorId() {
		return testCaseDescriptorId;
	}
	
	

	/**
	 * @param testCaseDescriptorId the testCaseDescriptorId to set
	 */
	public void setTestCaseDescriptorId(String testCaseDescriptorId) {
		this.testCaseDescriptorId = testCaseDescriptorId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the testCaseBlueprintId
	 */
	public String getTestCaseBlueprintId() {
		return testCaseBlueprintId;
	}

	/**
	 * @return the userParameters
	 */
	public Map<String, String> getUserParameters() {
		return userParameters;
	}

	/**
	 * @return the isPublic
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	
	
	@Override
    public void isValid() throws MalformattedElementException {
		if (name == null) throw new MalformattedElementException("TCD without name");
		if (version == null) throw new MalformattedElementException("TCD without version");
		if (testCaseBlueprintId == null) throw new MalformattedElementException("TCD without blueprint ID");
	}
}
