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
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either ctxress or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package it.nextworks.nfvmano.catalogue.blueprint.messages;

import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxDescriptor;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import java.util.ArrayList;
import java.util.List;

public class QueryCtxDescriptorResponse implements InterfaceMessage {

	private List<CtxDescriptor> ctxDescriptors = new ArrayList<>();

	public QueryCtxDescriptorResponse() { }

	public QueryCtxDescriptorResponse(List<CtxDescriptor> ctxDescriptors) {
		if (ctxDescriptors != null) this.ctxDescriptors = ctxDescriptors;
	}
	
	/**
	 * @return the ctxDescriptors
	 */
	public List<CtxDescriptor> getCtxDescriptors() {
		return ctxDescriptors;
	}

	@Override
	public void isValid() throws MalformattedElementException {
		for (CtxDescriptor ctxd : ctxDescriptors) ctxd.isValid();
	}

}
