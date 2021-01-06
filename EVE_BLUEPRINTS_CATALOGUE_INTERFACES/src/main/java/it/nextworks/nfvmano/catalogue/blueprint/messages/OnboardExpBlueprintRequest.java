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


import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnboardExpBlueprintRequest extends OnBoardBlueprintRequest {

	private ExpBlueprint expBlueprint;


	//key VSB atomic component id
	//value NSD with context elements
	private Map<String, Nsd> enhancedVsbs = new HashMap<>();


	//key ctxbid, value: componentId
	private Map<String, String> contextComponent;



	public OnboardExpBlueprintRequest() { }


	/**
	 * Constructor
	 * 
	 * @param expBlueprint
	 * @param nsds
	 * @param translationRules
	 */
	public OnboardExpBlueprintRequest(ExpBlueprint expBlueprint, 
			List<Nsd> nsds,
			List<VsdNsdTranslationRule> translationRules,
									  Map<String, Nsd> enhancedVsbs, Map<String, String> contextComponent) {
		super(nsds, translationRules);
		this.expBlueprint = expBlueprint;
		this.enhancedVsbs = enhancedVsbs;
		if(contextComponent!=null) this.contextComponent= contextComponent;
	}


	public Map<String, String> getContextComponent() {
		return contextComponent;
	}

	/**
	 * @return the expBlueprint
	 */
	public ExpBlueprint getExpBlueprint() {
		return expBlueprint;
	}


	public Map<String, Nsd> getEnhancedVsbs() {
		return enhancedVsbs;
	}

	@Override
	public void isValid() throws MalformattedElementException {
		super.isValid();
		if (expBlueprint == null) throw new MalformattedElementException("Onboard EXP blueprint request without EXP blueprint");
		else expBlueprint.isValid();

		for(Nsd nsd: enhancedVsbs.values()){
			nsd.isValid();
		}
	}

}
