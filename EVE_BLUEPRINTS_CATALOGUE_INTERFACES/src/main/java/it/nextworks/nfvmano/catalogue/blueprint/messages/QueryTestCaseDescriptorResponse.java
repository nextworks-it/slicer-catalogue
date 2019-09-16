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
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either ctxress or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package it.nextworks.nfvmano.catalogue.blueprint.messages;

import it.nextworks.nfvmano.catalogue.blueprint.elements.TestCaseDescriptor;
import it.nextworks.nfvmano.libs.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;

import java.util.ArrayList;
import java.util.List;

public class QueryTestCaseDescriptorResponse implements InterfaceMessage {

	private List<TestCaseDescriptor> testCaseDescriptors = new ArrayList<>();

	public QueryTestCaseDescriptorResponse() { }

	public QueryTestCaseDescriptorResponse(List<TestCaseDescriptor> testCaseDescriptors) {
		if (testCaseDescriptors != null) this.testCaseDescriptors = testCaseDescriptors;
	}
	
	/**
	 * @return the testCaseDescriptors
	 */
	public List<TestCaseDescriptor> getTestCaseDescriptors() {
		return testCaseDescriptors;
	}

	@Override
	public void isValid() throws MalformattedElementException {
		for (TestCaseDescriptor tcd : testCaseDescriptors) tcd.isValid();
	}

}
