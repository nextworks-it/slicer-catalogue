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
import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.libs.catalogues.interfaces.messages.OnBoardVnfPackageRequest;
import it.nextworks.nfvmano.libs.catalogues.interfaces.messages.OnboardAppPackageRequest;
import it.nextworks.nfvmano.libs.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.descriptors.nsd.Nsd;

import java.util.ArrayList;
import java.util.List;

public class OnboardCtxBlueprintRequest implements InterfaceMessage {

	private CtxBlueprint ctxBlueprint;
	private List<Nsd> nsds = new ArrayList<>();
	private List<VsdNsdTranslationRule> translationRules = new ArrayList<>();
	
	public OnboardCtxBlueprintRequest() { }
	
	/**
	 * @param ctxBlueprint
	 * @param nsds
	  * @param translationRules
	 */
	public OnboardCtxBlueprintRequest(CtxBlueprint ctxBlueprint,
									  List<Nsd> nsds,
									  List<VsdNsdTranslationRule> translationRules) {
		this.ctxBlueprint = ctxBlueprint;
		if (nsds != null) this.nsds = nsds;
		if (translationRules != null) this.translationRules = translationRules;
	}

	/**
	 * @return the ctxBlueprint
	 */
	public CtxBlueprint getCtxBlueprint() {
		return ctxBlueprint;
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
	public void setBlueprintIdInTranslationRules(String vsbId) {
		for (VsdNsdTranslationRule tr : translationRules) 
			tr.setVsbId(vsbId);
	}
	
	@JsonIgnore
	public void setNsdInfoIdInTranslationRules(String nsdInfoId, String nsdId, String nsdVersion) {
		for (VsdNsdTranslationRule tr : translationRules) {
			if (tr.matchesNsdIdAndNsdVersion(nsdId, nsdVersion)) tr.setNsdInfoId(nsdInfoId);
		}
	}

	@Override
	public void isValid() throws MalformattedElementException {
		if (ctxBlueprint == null) throw new MalformattedElementException("Onboard CTX blueprint request without CTX blueprint");
		else ctxBlueprint.isValid();

		if (translationRules.isEmpty()) throw new MalformattedElementException("Onboard CTX blueprint request without translation rules");
		for (Nsd nsd : nsds) nsd.isValid();
		for (VsdNsdTranslationRule tr : translationRules) tr.isValid();
	}

}
