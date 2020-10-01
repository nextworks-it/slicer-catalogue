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
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;
import it.nextworks.nfvmano.libs.ifa.catalogues.interfaces.messages.OnBoardVnfPackageRequest;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.templates.NST;

public class OnBoardNsTemplateRequest implements InterfaceMessage {

    private NST nst;
    private List<Nsd> nsds = new ArrayList<>();
	private List<OnBoardVnfPackageRequest> vnfPackages = new ArrayList<>();

    public OnBoardNsTemplateRequest() { }
	/**
	 * Constructor
	 * @param nst
	 * @param nsds
	 */

    public OnBoardNsTemplateRequest(NST nst,List<Nsd> nsds,List<OnBoardVnfPackageRequest> vnfPackages) {
    	if(this.nsds!=null)
			this.nsds = nsds;
        this.nst = nst;
		if(vnfPackages!=null)
			this.vnfPackages= vnfPackages;
    }

    @Override
	public void isValid() throws MalformattedElementException {
		if (nsds!=null && nsds.isEmpty()){
			for (Nsd nsd : nsds) nsd.isValid();
		}

		if(vnfPackages!=null && !vnfPackages.isEmpty()){
			for (OnBoardVnfPackageRequest vnf : vnfPackages) vnf.isValid();
		}

		if (nst == null) throw new MalformattedElementException("On board NS Template request without NS Template");
        else nst.isValid();

	}

	public NST getNst() {
		return nst;
	}

	public void setNst(NST nst) {
		this.nst = nst;
	}

	public List<Nsd> getNsds() {
		return nsds;
	}

	/**
	 * @return the vnfPackages
	 */
	public List<OnBoardVnfPackageRequest> getVnfPackages() {
		return vnfPackages;
	}
}
