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

import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardTestCaseBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryTestCaseBlueprintResponse;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;

public interface TestCaseBlueprintCatalogueInterface {

	/**
	 * Method to create a new test case blueprint
	 * 
	 * @param request TC blueprint creation request
	 * @return the ID of the TC blueprint
	 * @throws MethodNotImplementedException if the method is not implemented
	 * @throws MalformattedElementException if the request is malformatted
	 * @throws AlreadyExistingEntityException if a test case blueprint with same name and version is already available
	 * @throws FailedOperationException if the operation fails
	 */
	public String onboardTestCaseBlueprint(OnboardTestCaseBlueprintRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException;
	
	/**
	 * Method to retrieve a Test Case Blueprint from the catalogue.
	 * 
	 * @param request query
	 * @return the requested test case blueprint
	 * @throws MethodNotImplementedException if the method is not implemented
	 * @throws MalformattedElementException if the request is malformatted
	 * @throws NotExistingEntityException if the requested test case blueprint does not exist
	 * @throws FailedOperationException if the operation fails
	 */
	public QueryTestCaseBlueprintResponse queryTestCaseBlueprint(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;
	
	/**
	 * Method to delete a Test Case blueprint from the catalogue
	 * 
	 * @param testCaseBlueprintId ID of the test case blueprint to be removed
	 * @throws MethodNotImplementedException if the method is not implemented
	 * @throws MalformattedElementException if the ID is malformatted
	 * @throws NotExistingEntityException if the test case blueprint does not exist
	 * @throws FailedOperationException if the operation fails
	 */
	public void deleteTestCaseBlueprint(String testCaseBlueprintId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;
}
