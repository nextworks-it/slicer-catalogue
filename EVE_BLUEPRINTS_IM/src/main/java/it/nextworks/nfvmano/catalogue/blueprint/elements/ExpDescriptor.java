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
import com.fasterxml.jackson.annotation.JsonInclude;

import it.nextworks.nfvmano.libs.ifa.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import org.hibernate.annotations.*;


import javax.persistence.Entity;
import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class ExpDescriptor implements DescriptorInformationElement {

	@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
	
	private String expDescriptorId;
	private String name;
	private String version;
	private String expBlueprintId;

	private String vsDescriptorId;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> ctxDescriptorIds;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> testCaseDescriptorIds;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	//Key: kpi ID; Value: comparison operator + KPI threshold
	private Map<String,String> kpiThresholds = new HashMap<>();

	@JsonIgnore
	private boolean isPublic;
	
	@JsonIgnore
	private String tenantId;
	
	public ExpDescriptor() {	}
	
	

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param version
	 * @param expBlueprintId
	 * @param isPublic
	 * @param tenantId
	 * @param vsDescriptorId
	 * @param ctxDescriptorIds
	 * @param testCaseDescriptorIds
	 * @param kpiThresholds
	 */
	public ExpDescriptor(String name, 
			String version, 
			String expBlueprintId,
			boolean isPublic, 
			String tenantId,
			String vsDescriptorId,
			List<String> ctxDescriptorIds,
			List<String> testCaseDescriptorIds,
			Map<String,String> kpiThresholds) {
		this.name = name;
		this.version = version;
		this.expBlueprintId = expBlueprintId;
		this.isPublic = isPublic;
		this.tenantId = tenantId;
		this.vsDescriptorId = vsDescriptorId;
		if (ctxDescriptorIds != null) this.ctxDescriptorIds = ctxDescriptorIds;
		if (testCaseDescriptorIds != null) this.testCaseDescriptorIds = testCaseDescriptorIds;
		if (kpiThresholds != null) this.kpiThresholds = kpiThresholds;
	}



	/**
	 * @return the expDescriptorId
	 */
	public String getExpDescriptorId() {
		return expDescriptorId;
	}



	/**
	 * @param expDescriptorId the expDescriptorId to set
	 */
	public void setExpDescriptorId(String expDescriptorId) {
		this.expDescriptorId = expDescriptorId;
	}

	public String getVsDescriptorId() {
		return vsDescriptorId;
	}

	public List<String> getCtxDescriptorIds() {
		return ctxDescriptorIds;
	}


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
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
	 * @return the expBlueprintId
	 */
	public String getExpBlueprintId() {
		return expBlueprintId;
	}




	/**
	 * @return the tenantId
	 */
	@JsonIgnore
	public String getTenantId() {
		return tenantId;
	}



	/**
	 * @return the isPublic
	 */
	@JsonIgnore
	public boolean isPublic() {
		return isPublic;
	}

	

	/**
	 * @return the testCaseDescriptorIds
	 */
	public List<String> getTestCaseDescriptorIds() {
		return testCaseDescriptorIds;
	}



	/**
	 * @return the kpiThresholds
	 */
	public Map<String, String> getKpiThresholds() {
		return kpiThresholds;
	}



	@Override
	public void isValid() throws MalformattedElementException {
		if (name == null) throw new MalformattedElementException("ExpD without name");
		if (version == null) throw new MalformattedElementException("ExpD without version");
		if (expBlueprintId == null) throw new MalformattedElementException("ExpD without blueprint ID");
	}

}
