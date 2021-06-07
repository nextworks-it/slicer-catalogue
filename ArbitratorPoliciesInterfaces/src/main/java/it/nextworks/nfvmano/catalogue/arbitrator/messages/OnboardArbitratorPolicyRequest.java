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
package it.nextworks.nfvmano.catalogue.arbitrator.messages;






import it.nextworks.nfvmano.catalogue.arbitrator.im.ArbitratorPolicy;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import java.util.List;

public class OnboardArbitratorPolicyRequest implements InterfaceMessage {

	private List<ArbitratorPolicy> policies;


	public OnboardArbitratorPolicyRequest() { }

	public OnboardArbitratorPolicyRequest(List<ArbitratorPolicy> policies) {
		this.policies = policies;
	}

	public List<ArbitratorPolicy> getPolicies() {
		return policies;
	}

	@Override
	public void isValid() throws MalformattedElementException {
		for(ArbitratorPolicy policy : policies){
			if (policy==null) throw new MalformattedElementException("Onboard Arbitration Policy without policy");
		}

	}
}
