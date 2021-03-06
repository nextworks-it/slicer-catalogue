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

import it.nextworks.nfvmano.catalogue.blueprint.elements.EMBBServiceParameters;
import it.nextworks.nfvmano.catalogue.blueprint.elements.SliceServiceParameters;
import it.nextworks.nfvmano.catalogue.blueprint.elements.URLLCServiceParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NfvNsInstantiationInfo {

	private String nstId;
	private String nfvNsdId;
	private String nsdVersion;
	private String deploymentFlavourId;
	private String instantiationLevelId;
	private List<String> domainIds = new ArrayList<String>();


	private SliceServiceParameters sliceServiceParameters;



	// nsstId -> domainId
	private Map<String, String> nsstDomain = new HashMap<>();
	
	public NfvNsInstantiationInfo() {
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param nfvNsdId NSD ID
	 * @param nsdVersion NSD version
	 * @param deploymentFlavourId NS Deployment Flavour ID
	 * @param instantiationLevelId NS Instantiation Level ID
	 * @param domainIds list of domains where the NFV NS should be instantiated
	 * @param nsstDomain map between nsst ID and target domain ID
	 */
	public NfvNsInstantiationInfo(String nfvNsdId,
			String nsdVersion,
			String deploymentFlavourId,
			String instantiationLevelId,
			List<String> domainIds, Map<String, String> nsstDomain,
								  SliceServiceParameters sliceServiceParameters) {
		this.nfvNsdId = nfvNsdId;
		this.nsdVersion = nsdVersion;
		this.deploymentFlavourId = deploymentFlavourId;
		this.instantiationLevelId = instantiationLevelId;
		if (domainIds != null) this.domainIds = domainIds;
		if (nsstDomain != null) this.nsstDomain = nsstDomain;
		if(sliceServiceParameters!=null) this.sliceServiceParameters = sliceServiceParameters;

	}
	
	/**
	 * Constructor
	 * 
	 * @param nstId NST ID
	 * @param nfvNsdId NSD ID
	 * @param nsdVersion NSD version
	 * @param deploymentFlavourId NS Deployment Flavour ID
	 * @param instantiationLevelId NS Instantiation Level ID
	 * @param domainIds list of domains where the NFV NS should be instantiated
	 * @param nsstDomain map between nsst ID and target domain ID
	 */
	public NfvNsInstantiationInfo(String nstId,
			String nfvNsdId,
			String nsdVersion,
			String deploymentFlavourId,
			String instantiationLevelId,
			List<String> domainIds,
								  Map<String, String> nsstDomain,
			SliceServiceParameters sliceServiceParameters) {
		this.nstId=nstId;
		this.nfvNsdId = nfvNsdId;
		this.nsdVersion = nsdVersion;
		this.deploymentFlavourId = deploymentFlavourId;
		this.instantiationLevelId = instantiationLevelId;
		if (domainIds != null) this.domainIds = domainIds;
		if (nsstDomain != null) this.nsstDomain = nsstDomain;
		if(sliceServiceParameters!=null) this.sliceServiceParameters = sliceServiceParameters;
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

	public void setNsstDomain(Map<String, String> nsstDomain) {
		this.nsstDomain = nsstDomain;
	}

	public String getNstId() {
		return nstId;
	}

	public SliceServiceParameters getSliceServiceParameters() {
		return sliceServiceParameters;
	}

	/**
	 * @return the domainIds
	 */
	public List<String> getDomainIds() {
		return domainIds;
	}
	/**
	 * @param domainIds the domainIds to set
	 */
	public void setDomainIds(List<String> domainIds) {
		this.domainIds = domainIds;
	}
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

	public Map<String, String> getNsstDomain() {
		return nsstDomain;
	}
}
