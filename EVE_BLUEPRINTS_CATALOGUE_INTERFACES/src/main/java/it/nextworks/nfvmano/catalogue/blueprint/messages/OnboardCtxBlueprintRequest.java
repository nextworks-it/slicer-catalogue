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

import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.descriptors.nsd.Nsd;


import java.util.List;

public class OnboardCtxBlueprintRequest extends OnBoardBlueprintRequest {

	private CtxBlueprint ctxBlueprint;
	
	public OnboardCtxBlueprintRequest() { }
	
	/**
	 * @param ctxBlueprint
	 * @param nsds
	  * @param translationRules
	 */
	public OnboardCtxBlueprintRequest(CtxBlueprint ctxBlueprint,
									  List<Nsd> nsds,
									  List<VsdNsdTranslationRule> translationRules) {
		super(nsds, translationRules);
		this.ctxBlueprint = ctxBlueprint;
	}

	/**
	 * @return the ctxBlueprint
	 */
	public CtxBlueprint getCtxBlueprint() {
		return ctxBlueprint;
	}

	@Override
	public void isValid() throws MalformattedElementException {
		super.isValid();
		if (ctxBlueprint == null) throw new MalformattedElementException("Onboard CTX blueprint request without CTX blueprint");
		else ctxBlueprint.isValid();
	}

}
