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

import it.nextworks.nfvmano.libs.ifa.catalogues.interfaces.messages.OnBoardVnfPackageRequest;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.libs.ifa.templates.NST;

public class OnBoardVsBlueprintRequest extends OnBoardBlueprintRequest {

	private VsBlueprint vsBlueprint;
	private List<OnBoardVnfPackageRequest> vnfPackages = new ArrayList<>();
	public OnBoardVsBlueprintRequest() { }
	
	/**
	 * Constructor
	 * 
	 * @param vsBlueprint
	 * @param nsds
	 * @param translationRules
	 */
	public OnBoardVsBlueprintRequest(VsBlueprint vsBlueprint, 
			List<Nsd> nsds,
			List<VsdNsdTranslationRule> translationRules,  List<OnBoardVnfPackageRequest> vnfPackages,
									 List<NST> nsts) {
		super(nsds, translationRules, nsts);
		if(vnfPackages!=null) this.vnfPackages= vnfPackages;
		this.vsBlueprint = vsBlueprint;
	}

	/**
	 * @return the vsBlueprint
	 */
	public VsBlueprint getVsBlueprint() {
		return vsBlueprint;
	}

	@Override
	public void isValid() throws MalformattedElementException {
		super.isValid();
		for (OnBoardVnfPackageRequest vnf : vnfPackages) vnf.isValid();
		if (vsBlueprint == null) throw new MalformattedElementException("Onboard VS blueprint request without VS blueprint");
		else vsBlueprint.isValid();
	}

	/**
	 * @return the vnfPackages
	 */
	public List<OnBoardVnfPackageRequest> getVnfPackages() {
		return vnfPackages;
	}


}
