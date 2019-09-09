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

import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsDescriptor;
import it.nextworks.nfvmano.libs.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;

import java.util.List;

public class OnboardExpDescriptorRequest implements InterfaceMessage {

	private ExpDescriptor expd;
	private String tenantId;
	private boolean isPublic;


	private VsDescriptor vsDescriptor;
	private List<CtxDescriptor> ctxDescriptors;

	public OnboardExpDescriptorRequest() { }

	public OnboardExpDescriptorRequest(ExpDescriptor expd, String tenantId, boolean isPublic, VsDescriptor vsDescriptor, List<CtxDescriptor> ctxDescriptors) {
		this.expd = expd;
		this.tenantId = tenantId;
		this.isPublic = isPublic;
		this.vsDescriptor = vsDescriptor;
		this.ctxDescriptors = ctxDescriptors;
	}

	/**
	 * @return the expd
	 */
	public ExpDescriptor getExpd() {
		return expd;
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
		if (expd == null) throw new MalformattedElementException("Onboard ExpD request without ExpD");
		else expd.isValid();
		if (tenantId == null) throw new MalformattedElementException("Onboard ExpD request without tenant ID");
	}

}
