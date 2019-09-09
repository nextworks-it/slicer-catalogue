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
import it.nextworks.nfvmano.libs.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class CtxDescriptor implements DescriptorInformationElement {




	@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

	private String ctxDescriptorId;
	private String name;
	private String version;
	private String ctxBlueprintId;

	//Key: parameter ID as in the blueprint; value: desired value
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private Map<String, String> ctxParameters = new HashMap<String, String>();
	

	@JsonIgnore
	private boolean isPublic;

	@JsonIgnore
	private String tenantId;

	

	

	

	public CtxDescriptor() {	}



	/**
	 * @param name
	 * @param version
	 * @param ctxBlueprintId
	 * @param sst
	 * @param managementType
	 * @param isPublic
	 * @param tenantId
	 *
	 */
	public CtxDescriptor(String name, String version, String ctxBlueprintId, SliceServiceType sst,
                         SliceManagementControlType managementType, Map<String, String> ctxParameters,
                         boolean isPublic, String tenantId) {
		this.name = name;
		this.version = version;
		this.ctxBlueprintId = ctxBlueprintId;
		this.isPublic = isPublic;
		this.tenantId = tenantId;
	}


	/**
	 * @param ctxBlueprintId the ctxBlueprintId to set
	 */
	public void setCtxBlueprintId(String ctxBlueprintId) {
		this.ctxBlueprintId = ctxBlueprintId;
	}



	/**
	 * @return the ctxDescriptorId
	 */
	public String getCtxDescriptorId() {
		return ctxDescriptorId;
	}



	/**
	 * @param ctxDescriptorId the ctxDescriptorId to set
	 */
	public void setCtxDescriptorId(String ctxDescriptorId) {
		this.ctxDescriptorId = ctxDescriptorId;
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
	 * @return the ctxBlueprintId
	 */
	public String getCtxBlueprintId() {
		return ctxBlueprintId;
	}




	/**
	 * @return the qosParameters
	 */
	public Map<String, String> getCtxParameters() {
		return ctxParameters;
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
	 * @param isPublic the isPublic to set
	 */
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}






	@Override
	public void isValid() throws MalformattedElementException {
		if (name == null) throw new MalformattedElementException("VSD without name");
		if (version == null) throw new MalformattedElementException("VSD without version");
		if (ctxBlueprintId == null) throw new MalformattedElementException("VSD without VS blueprint ID");

	}

}
