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


import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnboardVsDescriptorRequest implements InterfaceMessage {

	private VsDescriptor vsd;
	private String tenantId;
	private boolean isPublic;


	//the key is the component id
	private Map<String, VsDescriptor> nestedVsd = new HashMap<>();
	
	public OnboardVsDescriptorRequest() { }


	/**
	 * @param vsd
	 * @param tenantId
	 * @param isPublic
	 *
	 */
	public OnboardVsDescriptorRequest(VsDescriptor vsd, String tenantId, boolean isPublic) {
		this.vsd = vsd;
		this.tenantId = tenantId;
		this.isPublic = isPublic;

	}


	/**
	 * @param vsd
	 * @param tenantId
	 * @param isPublic
	 * @param nestedVsd Containing a Map of parameters to create the nested VSDs
	 */
	public OnboardVsDescriptorRequest(VsDescriptor vsd, String tenantId, boolean isPublic, Map<String,VsDescriptor> nestedVsd) {
		this.vsd = vsd;
		this.tenantId = tenantId;
		this.isPublic = isPublic;
		if(nestedVsd!=null) this.nestedVsd = nestedVsd;
	}

	public Map<String, VsDescriptor> getNestedVsd() {
		return nestedVsd;
	}

	/**
	 * @return the vsd
	 */
	public VsDescriptor getVsd() {
		return vsd;
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



	@Override
	public void isValid() throws MalformattedElementException {
		if (vsd == null) throw new MalformattedElementException("Onboard VSD request without VSD");
		else vsd.isValid();
		if (tenantId == null) throw new MalformattedElementException("Onboard VSD request without tenant ID");
	}

}
