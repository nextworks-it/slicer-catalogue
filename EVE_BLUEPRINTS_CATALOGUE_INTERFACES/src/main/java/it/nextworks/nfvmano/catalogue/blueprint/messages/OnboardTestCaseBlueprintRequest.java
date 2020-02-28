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
import it.nextworks.nfvmano.catalogue.blueprint.elements.TestCaseBlueprint;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

public class OnboardTestCaseBlueprintRequest implements InterfaceMessage {

	private TestCaseBlueprint testCaseBlueprint;

	private String owner;
	
	public OnboardTestCaseBlueprintRequest() {	}
	
	public OnboardTestCaseBlueprintRequest(TestCaseBlueprint testCaseBlueprint) {
		this.testCaseBlueprint = testCaseBlueprint;
	}


	@JsonIgnore
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the testCaseBlueprint
	 */
	public TestCaseBlueprint getTestCaseBlueprint() {
		return testCaseBlueprint;
	}

	@Override
	public void isValid() throws MalformattedElementException {
		if (testCaseBlueprint == null) throw new MalformattedElementException("Onboard test case blueprint request without blueprint");
		else testCaseBlueprint.isValid();
	}
}
