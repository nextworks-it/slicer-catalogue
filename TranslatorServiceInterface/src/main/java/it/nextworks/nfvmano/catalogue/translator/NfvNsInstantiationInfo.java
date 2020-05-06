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
package it.nextworks.nfvmano.catalogue.translator;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class NfvNsInstantiationInfo {

	private String nstId;
	private String nfvNsdId;
	private String nsdVersion;
	private String deploymentFlavourId;
	private String instantiationLevelId;

	/*@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch= FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> domainIds = new ArrayList<String>();
	*/
	public NfvNsInstantiationInfo() {
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param nfvNsdId NSD ID
	 * @param nsdVersion NSD version
	 * @param deploymentFlavourId NS Deployment Flavour ID
	 * @param instantiationLevelId NS Instantiation Level ID
	 * @param domains list of domains where the NFV NS should be instantiated
	 */
	public NfvNsInstantiationInfo(String nfvNsdId,
			String nsdVersion,
			String deploymentFlavourId,
			String instantiationLevelId,
			List<String> domainIds) {
		this.nfvNsdId = nfvNsdId;
		this.nsdVersion = nsdVersion;
		this.deploymentFlavourId = deploymentFlavourId;
		this.instantiationLevelId = instantiationLevelId;
		//if (domainIds != null) this.domainIds = domainIds;
	}


	/**
	 * Constructor
	 * 
	 * @param nstId NST ID
	 * @param nfvNsdId NSD ID
	 * @param nsdVersion NSD version
	 * @param deploymentFlavourId NS Deployment Flavour ID
	 * @param instantiationLevelId NS Instantiation Level ID
	 * @param domains list of domains where the NFV NS should be instantiated
	 */
	public NfvNsInstantiationInfo(String nstId,
			String nfvNsdId,
			String nsdVersion,
			String deploymentFlavourId,
			String instantiationLevelId,
			List<String> domainIds) {
		this.nstId=nstId;
		this.nfvNsdId = nfvNsdId;
		this.nsdVersion = nsdVersion;
		this.deploymentFlavourId = deploymentFlavourId;
		this.instantiationLevelId = instantiationLevelId;
	//	if (domainIds != null) this.domainIds = domainIds;
	}


	public NfvNsInstantiationInfo(String nstId) {
		this.nstId=nstId;
	}

	public void setNstId(String nstId) {
		this.nstId = nstId;
	}
	public void setNfvNsdId(String nfvNsdId) {
		this.nfvNsdId = nfvNsdId;
	}

	public void setNsdVersion(String nsdVersion) {
		this.nsdVersion = nsdVersion;
	}

	public void setDeploymentFlavourId(String deploymentFlavourId) {
		this.deploymentFlavourId = deploymentFlavourId;
	}

	public void setInstantiationLevelId(String instantiationLevelId) {
		this.instantiationLevelId = instantiationLevelId;
	}
	
	

	public String getNstId() {
		return nstId;
	}
	/**
	 * @return the domainIds
	 */
	/*
	public List<String> getDomainIds() {
		return domainIds;
	}
	/**
	 * @param domainId the domainIds to set
	 */
	/*public void setDomainIds(List<String> domainIds) {
		this.domainIds = domainIds;
	}
	/*
	/**
	 * @return the nfvNsdId
	 */
	public String getNfvNsdId() {
		return nfvNsdId;
	}
	
	
	
	/**
	 * @return the nsdVersion
	 */
	public String getNsdVersion() {
		return nsdVersion;
	}

	/**
	 * @return the deploymentFlavourId
	 */
	public String getDeploymentFlavourId() {
		return deploymentFlavourId;
	}
	
	/**
	 * @return the instantiationLevelId
	 */
	public String getInstantiationLevelId() {
		return instantiationLevelId;
	}
	
}
