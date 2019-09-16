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

import com.fasterxml.jackson.annotation.JsonInclude;

import it.nextworks.nfvmano.libs.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;

public class BlueprintUserInformation implements DescriptorInformationElement {
	
	private String blueprintId;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Map<String, String> parameters = new HashMap<String, String>();
	
	public BlueprintUserInformation() {	}
	
	public BlueprintUserInformation(String blueprintId,
			Map<String, String> parameters) {
		this.blueprintId = blueprintId;
		if (parameters != null) this.parameters = parameters;
	}
	
	
	
	/**
	 * @return the blueprintId
	 */
	public String getBlueprintId() {
		return blueprintId;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
    public void isValid() throws MalformattedElementException {
		if (blueprintId == null) throw new MalformattedElementException("Blueprint user information without blueprint ID.");
	}

}
