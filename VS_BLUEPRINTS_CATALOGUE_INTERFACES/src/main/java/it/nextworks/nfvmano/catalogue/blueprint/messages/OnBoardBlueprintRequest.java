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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;

public class OnBoardBlueprintRequest implements InterfaceMessage {

	private List<Nsd> nsds = new ArrayList<>();
	private List<VsdNsdTranslationRule> translationRules = new ArrayList<>();
	private String owner;
	
	public OnBoardBlueprintRequest() { }
	
	/**
	 * Constructor 
	 * 
	 * @param nsds
	 * @param translationRules
	 */
	public OnBoardBlueprintRequest(List<Nsd> nsds,
			List<VsdNsdTranslationRule> translationRules) {
		if (nsds != null) this.nsds = nsds;
		if (translationRules != null) this.translationRules = translationRules;
	}

	
	/**
	 * @return the nsds
	 */
	public List<Nsd> getNsds() {
		return nsds;
	}

	/**
	 * @return the translationRules
	 */
	public List<VsdNsdTranslationRule> getTranslationRules() {
		return translationRules;
	}
	
	@JsonIgnore
	public void setBlueprintIdInTranslationRules(String blueprintId) {
		for (VsdNsdTranslationRule tr : translationRules) 
			tr.setBlueprintId(blueprintId);
	}
	
	@JsonIgnore
	public void setNsdInfoIdInTranslationRules(String nsdInfoId, String nsdId, String nsdVersion) {
		for (VsdNsdTranslationRule tr : translationRules) {
			if (tr.matchesNsdIdAndNsdVersion(nsdId, nsdVersion)) tr.setNsdInfoId(nsdInfoId);
		}
	}

	@Override
	public void isValid() throws MalformattedElementException {
		//if (nsds.isEmpty()) throw new MalformattedElementException("Onboard VS blueprint request without NSD");
		if (translationRules.isEmpty()) throw new MalformattedElementException("Onboard VS blueprint request without translation rules");
		else for (VsdNsdTranslationRule tr : translationRules) tr.isValid(); 
		for (Nsd nsd : nsds) nsd.isValid();
	}

	@JsonIgnore
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
