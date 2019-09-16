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
package it.nextworks.nfvmano.catalogue.blueprint.interfaces;

import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardTestCaseDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryTestCaseDescriptorResponse;
import it.nextworks.nfvmano.libs.common.exceptions.*;
import it.nextworks.nfvmano.libs.common.messages.GeneralizedQueryRequest;

public interface TestCaseDescriptorCatalogueInterface {

	/**
	 * Method to create a new test case descriptor
	 * 
	 * @param request
	 * @return
	 * @throws MethodNotImplementedException
	 * @throws MalformattedElementException
	 * @throws AlreadyExistingEntityException
	 * @throws FailedOperationException
	 */
	public String onboardTestCaseDescriptor(OnboardTestCaseDescriptorRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException;
	
	/**
	 * Method to request info about an existing test case descriptor
	 * 
	 * @param request
	 * @return
	 * @throws MethodNotImplementedException
	 * @throws MalformattedElementException
	 * @throws NotExistingEntityException
	 * @throws FailedOperationException
	 */
	public QueryTestCaseDescriptorResponse queryTestCaseDescriptor(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;
	
	/**
	 * Method to remove a test case descriptor
	 * 
	 * @param testcaseDescriptorId
	 * @param tenantId
	 * @throws MethodNotImplementedException
	 * @throws MalformattedElementException
	 * @throws NotExistingEntityException
	 * @throws FailedOperationException
	 */
	public void deleteTestCaseDescriptor(String testcaseDescriptorId, String tenantId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;

}
