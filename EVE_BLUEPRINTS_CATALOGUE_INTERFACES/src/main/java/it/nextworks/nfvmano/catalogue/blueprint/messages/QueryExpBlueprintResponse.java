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

import com.fasterxml.jackson.annotation.JsonInclude;
import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpBlueprintInfo;
import it.nextworks.nfvmano.libs.ifa.catalogues.interfaces.elements.NsdInfo;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import java.util.ArrayList;
import java.util.List;

public class QueryExpBlueprintResponse implements InterfaceMessage {

	private List<ExpBlueprintInfo> expBlueprintInfo = new ArrayList<>();


	public QueryExpBlueprintResponse() {	}

	public QueryExpBlueprintResponse(List<ExpBlueprintInfo> expBlueprintInfo) {
		if (expBlueprintInfo != null) this.expBlueprintInfo = expBlueprintInfo;

	}
	
	/**
	 * @return the expBlueprintInfo
	 */
	public List<ExpBlueprintInfo> getExpBlueprintInfo() {
		return expBlueprintInfo;
	}




	@Override
	public void isValid() throws MalformattedElementException {	
		for (ExpBlueprintInfo expBi : expBlueprintInfo) expBi.isValid();
	}

}
