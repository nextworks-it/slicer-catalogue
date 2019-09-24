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

import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardExpBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryExpBlueprintResponse;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;


public interface ExpBlueprintCatalogueInterface {

	/**
	 * Method to create a new Experiment Blueprint, including all the corresponding elements on the service orchestrator side.
	 * 
	 * @param request Experiment Blueprint creation request
	 * @return the ID of the Exp Blueprint
	 * @throws MethodNotImplementedException if the method is not implemented
	 * @throws MalformattedElementException if the request is malformatted
	 * @throws AlreadyExistingEntityException if the Exp blueprint already exists
	 * @throws FailedOperationException if the operation fails
	 */
	public String onboardExpBlueprint(OnboardExpBlueprintRequest request)
            throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException, NotExistingEntityException;
	
	/**
	 * Method to retrieve a Exp Blueprint from the catalogue.
	 * 
	 * @param request query
	 * @return the Exp blueprint info
	 * @throws MethodNotImplementedException if the method is not implemented
	 * @throws MalformattedElementException if the request is malformatted
	 * @throws NotExistingEntityException if the Exp blueprint does not exist
	 * @throws FailedOperationException if the operation fails
	 */
	public QueryExpBlueprintResponse queryExpBlueprint(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;
	
	/**
	 * Method to delete a Exp blueprint from the catalogue
	 * 
	 * @param expBlueprintId ID of the Exp blueprint to be removed
	 * @throws MethodNotImplementedException if the method is not implemented
	 * @throws MalformattedElementException if the ID is malformatted
	 * @throws NotExistingEntityException if the Exp blueprint does not exist
	 * @throws FailedOperationException if the operation fails
	 */
	public void deleteExpBlueprint(String expBlueprintId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;

	
}
