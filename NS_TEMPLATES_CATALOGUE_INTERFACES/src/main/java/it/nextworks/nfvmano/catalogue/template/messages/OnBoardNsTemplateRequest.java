/*
 * Copyright (c) 2019 Nextworks s.r.l
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.nextworks.nfvmano.catalogue.template.messages;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.nextworks.nfvmano.catalogue.template.elements.NstConfigurationRule;
import it.nextworks.nfvmano.libs.ifasol.catalogues.interfaces.messages.OnboardNsdRequest;
import it.nextworks.nfvmano.libs.ifasol.catalogues.interfaces.messages.OnboardPnfdRequest;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;
import it.nextworks.nfvmano.libs.ifasol.catalogues.interfaces.messages.OnBoardVnfPackageRequest;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Pnfd;
import it.nextworks.nfvmano.libs.ifa.templates.NST;

public class OnBoardNsTemplateRequest implements InterfaceMessage {

    private NST nst;
    private List<OnboardNsdRequest> nsds = new ArrayList<>();
	private List<OnBoardVnfPackageRequest> vnfPackages = new ArrayList<>();
	//private List<OnboardPnfdRequest> pnfds = new ArrayList<>();

	private List<NstConfigurationRule> configurationRules = new ArrayList<>();

    public OnBoardNsTemplateRequest() { }
	/**
	 * Constructor
	 * @param nst
	 * @param nsds
	 */

	public OnBoardNsTemplateRequest(NST nst, List<OnboardNsdRequest> nsds, List<OnBoardVnfPackageRequest> vnfPackages, List<OnboardPnfdRequest> pnfds, List<NstConfigurationRule> configurationRules) {
    	if(this.nsds!=null)
			this.nsds = nsds;
        this.nst = nst;
		if(vnfPackages!=null)
			this.vnfPackages= vnfPackages;
		/*if(pnfds!=null)
			this.pnfds = pnfds;*/
		if (configurationRules != null) this.configurationRules = configurationRules;
    }

    @Override
	public void isValid() throws MalformattedElementException {
		if (nsds!=null && nsds.isEmpty()){
			for (OnboardNsdRequest nsd : nsds) nsd.isValid();
		}

		if(vnfPackages!=null && !vnfPackages.isEmpty()){
			for (OnBoardVnfPackageRequest vnf : vnfPackages) vnf.isValid();
		}

		if (nst == null) throw new MalformattedElementException("On board NS Template request without NS Template");
        else nst.isValid();

        /*if(pnfds != null && !pnfds.isEmpty()){
        	for (OnboardPnfdRequest pnfd : pnfds) pnfd.isValid();
		}*/
		if(!configurationRules.isEmpty()) for (NstConfigurationRule r : configurationRules) r.isValid();
	}

	public NST getNst() {
		return nst;
	}

	public void setNst(NST nst) {
		this.nst = nst;
	}

	public List<OnboardNsdRequest> getNsds() {
		return nsds;
	}

	//public List<OnboardPnfdRequest> getPnfds() { return pnfds; }

	/**
	 * @return the vnfPackages
	 */
	public List<OnBoardVnfPackageRequest> getVnfPackages() {
		return vnfPackages;
	}

	public List<NstConfigurationRule> getConfigurationRules() {
		return configurationRules;
	}

	@JsonIgnore
	public void setNstIdInConfigurationRules(String nstId) {
		for (NstConfigurationRule cr : configurationRules)
			cr.setNstId(nstId);
	}
}
