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
package it.nextworks.nfvmano.catalogue.blueprint.messages;

import it.nextworks.nfvmano.catalogue.blueprint.elements.BlueprintUserInformation;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsDescriptor;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnboardExpDescriptorRequest implements InterfaceMessage {

	private String name;
	private String version;
	private String experimentBlueprintId;
		
	private String tenantId;
	private boolean isPublic;

	private VsDescriptor vsDescriptor;
	
	private List<BlueprintUserInformation> contextDetails = new ArrayList<>();
	private List<BlueprintUserInformation> testCaseConfiguration = new ArrayList<>();
	
	private Map<String,String> kpiThresholds = new HashMap<>();
	
	public OnboardExpDescriptorRequest() { }

	public OnboardExpDescriptorRequest(String name,
			String version,
			String experimentBlueprintId,
			String tenantId,
			boolean isPublic,
			VsDescriptor vsDescriptor,
			List<BlueprintUserInformation> contextDetails,
			List<BlueprintUserInformation> testCaseConfiguration,
			Map<String,String> kpiThresholds) {
		this.name = name;
		this.version = version;
		this.experimentBlueprintId = experimentBlueprintId;
		this.tenantId = tenantId;
		this.isPublic = isPublic;
		this.vsDescriptor = vsDescriptor;
		if (contextDetails != null) this.contextDetails = contextDetails;
	    if (testCaseConfiguration != null) this.testCaseConfiguration = testCaseConfiguration;
	    if (kpiThresholds != null) this.kpiThresholds = kpiThresholds;
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
	 * @return the experimentBlueprintId
	 */
	public String getExperimentBlueprintId() {
		return experimentBlueprintId;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @return the isPublic
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * @return the vsDescriptor
	 */
	public VsDescriptor getVsDescriptor() {
		return vsDescriptor;
	}

	/**
	 * @return the contextDetails
	 */
	public List<BlueprintUserInformation> getContextDetails() {
		return contextDetails;
	}

	/**
	 * @return the testCaseConfiguration
	 */
	public List<BlueprintUserInformation> getTestCaseConfiguration() {
		return testCaseConfiguration;
	}
	
	

	/**
	 * @return the kpiThresholds
	 */
	public Map<String, String> getKpiThresholds() {
		return kpiThresholds;
	}

	@Override
	public void isValid() throws MalformattedElementException {
		if (tenantId == null) throw new MalformattedElementException("Onboard ExpD request without tenant ID");
		if (name == null) throw new MalformattedElementException("Onboard ExpD request without name");
		if (version == null) throw new MalformattedElementException("Onboard ExpD request without version");
		if (experimentBlueprintId == null) throw new MalformattedElementException("Onboard ExpD request without blueprint ID");
		if (vsDescriptor == null) throw new MalformattedElementException("Onboard ExpD request without Vertical Service Descriptor");
			else vsDescriptor.isValid();
		for (BlueprintUserInformation cbi : contextDetails) cbi.isValid();
		for (BlueprintUserInformation tbi : testCaseConfiguration) tbi.isValid();
	}

}
