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

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNstTranslationRule;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import java.util.ArrayList;
import java.util.List;

public class OnBoardBlueprintRequest implements InterfaceMessage {

	private List<VsdNstTranslationRule> translationRules = new ArrayList<>();
	
	public OnBoardBlueprintRequest() { }
	
	/**
	 * Constructor 
	 *
	 * @param translationRules
	 */
	public OnBoardBlueprintRequest(List<VsdNstTranslationRule> translationRules) {
		if (translationRules != null) this.translationRules = translationRules;
	}


	/**
	 * @return the translationRules
	 */
	public List<VsdNstTranslationRule> getTranslationRules() {
		return translationRules;
	}
	
	@JsonIgnore
	public void setBlueprintIdInTranslationRules(String blueprintId) {
		for (VsdNstTranslationRule tr : translationRules)
			tr.setBlueprintId(blueprintId);
	}


	@Override
	public void isValid() throws MalformattedElementException {
		if (translationRules.isEmpty()) throw new MalformattedElementException("On board VS blueprint request without any translation rules");
		else for (VsdNstTranslationRule tr : translationRules) tr.isValid();
	}

}
