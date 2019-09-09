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
import com.fasterxml.jackson.annotation.JsonInclude;
import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.libs.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.descriptors.nsd.Nsd;

import java.util.ArrayList;
import java.util.List;

public class OnboardExpBlueprintRequest implements InterfaceMessage {

	private ExpBlueprint expBlueprint;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Nsd> nsds;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<VsdNsdTranslationRule> expTranslationRules = new ArrayList<>();

	public OnboardExpBlueprintRequest() { }


	public OnboardExpBlueprintRequest(ExpBlueprint expBlueprint, List<Nsd> nsds, List<VsdNsdTranslationRule> expTranslationRules) {
		this.expBlueprint = expBlueprint;
		this.nsds = nsds;
		if (expTranslationRules!=null)
			this.expTranslationRules =expTranslationRules;


	}


	/**
	 * @return the expBlueprint
	 */
	public ExpBlueprint getExpBlueprint() {
		return expBlueprint;
	}


	@Override
	public void isValid() throws MalformattedElementException {
		if (expBlueprint == null) throw new MalformattedElementException("Onboard EXP blueprint request without EXP blueprint");
		else{
			expBlueprint.isValid();
			for(Nsd nsd : nsds){
				nsd.isValid();
			}
			for(VsdNsdTranslationRule tr : expTranslationRules){
				tr.isValid();
			}


		}


	}

	@JsonIgnore
	public void setNsdInfoIdInTranslationRules(String nsdInfoId, String nsdId, String nsdVersion) {
		for (VsdNsdTranslationRule tr : expTranslationRules) {
			if (tr.matchesNsdIdAndNsdVersion(nsdId, nsdVersion)) tr.setNsdInfoId(nsdInfoId);
		}
	}

	public List<Nsd> getNsds() {
		return nsds;
	}

	public List<VsdNsdTranslationRule> getExpTranslationRules() {
		return expTranslationRules;
	}




}
