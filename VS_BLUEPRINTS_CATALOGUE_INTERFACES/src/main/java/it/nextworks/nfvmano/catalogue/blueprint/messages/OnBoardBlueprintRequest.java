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
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNstTranslationRule;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;

import java.util.ArrayList;
import java.util.List;

public class OnBoardBlueprintRequest implements InterfaceMessage {

	private List<VsdNstTranslationRule> translationRules = new ArrayList<>();

	private List<Nsd> nsds = new ArrayList<>();
	private List<VsdNsdTranslationRule> nsdTranslationRules = new ArrayList<>();

	public OnBoardBlueprintRequest() { }

	/**
	 * Constructor
	 *
	 * @param nsds
	 * @param nsdTranslationRules
	 */
	public OnBoardBlueprintRequest(List<Nsd> nsds,
								   List<VsdNsdTranslationRule> nsdTranslationRules) {
		if (nsds != null) this.nsds = nsds;
		if (translationRules != null) this.nsdTranslationRules = nsdTranslationRules;
	}

	/**
	 * @return the nsds
	 */
	public List<Nsd> getNsds() {
		return nsds;
	}

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
	public List<VsdNsdTranslationRule> getTranslationRules() {
		return nsdTranslationRules;
	}

	public List<VsdNstTranslationRule> getNstTranslationRules() {
		return translationRules;
	}

	@JsonIgnore
	public void setBlueprintIdInTranslationRules(String blueprintId) {
		for (VsdNstTranslationRule tr : translationRules)
			tr.setBlueprintId(blueprintId);
	}



	@JsonIgnore
	public void setNsdInfoIdInTranslationRules(String nsdInfoId, String nsdId, String nsdVersion) {
		for (VsdNsdTranslationRule tr : nsdTranslationRules) {
			if (tr.matchesNsdIdAndNsdVersion(nsdId, nsdVersion)) tr.setNsdInfoId(nsdInfoId);
		}
	}

	@Override
	public void isValid() throws MalformattedElementException {
		if (translationRules.isEmpty()) throw new MalformattedElementException("On board VS blueprint request without any translation rules");
		else for (VsdNstTranslationRule tr : translationRules) tr.isValid();
	}

}
