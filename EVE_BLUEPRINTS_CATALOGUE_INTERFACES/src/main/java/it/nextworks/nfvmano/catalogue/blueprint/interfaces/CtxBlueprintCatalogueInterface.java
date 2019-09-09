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

import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryCtxBlueprintResponse;
import it.nextworks.nfvmano.libs.common.exceptions.*;
import it.nextworks.nfvmano.libs.common.messages.GeneralizedQueryRequest;

import java.util.Optional;

public interface CtxBlueprintCatalogueInterface {

	/**
	 * Method to create a new CTX Blueprint, including all the corresponding elements on the service orchestrator side.
	 * 
	 * @param request CTX Blueprint creation request
	 * @return the ID of the CTX Blueprint
	 * @throws MethodNotImplementedException if the method is not implemented
	 * @throws MalformattedElementException if the request is malformatted
	 * @throws AlreadyExistingEntityException if the CTX blueprint already exists
	 * @throws FailedOperationException if the operation fails
	 */
	public String onboardCtxBlueprint(OnboardCtxBlueprintRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException;
	
	/**
	 * Method to retrieve a CTX Blueprint from the catalogue.
	 * 
	 * @param request query
	 * @return the CTX blueprint info
	 * @throws MethodNotImplementedException if the method is not implemented
	 * @throws MalformattedElementException if the request is malformatted
	 * @throws NotExistingEntityException if the CTX blueprint does not exist
	 * @throws FailedOperationException if the operation fails
	 */
	public QueryCtxBlueprintResponse queryCtxBlueprint(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;
	
	/**
	 * Method to delete a CTX blueprint from the catalogue
	 * 
	 * @param ctxBlueprintId ID of the CTX blueprint to be removed
	 * @throws MethodNotImplementedException if the method is not implemented
	 * @throws MalformattedElementException if the ID is malformatted
	 * @throws NotExistingEntityException if the CTX blueprint does not exist
	 * @throws FailedOperationException if the operation fails
	 */
	public void deleteCtxBlueprint(String ctxBlueprintId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;

	/**
	 * Method to retrieve the CtxBlueprint element from the catalogue
	 * @param ctxBlueprintId ID of the blueprint to retrieve
	 * @return the CTX Blueprint element
	 */

	Optional<CtxBlueprint> findByCtxBlueprintId(String ctxBlueprintId);


	/**
	 * Method to retrieve the first CtxBlueprint matching the name and version specified
	 * @param name Name of the VS Blueprint
	 * @param version Version of the VS Blueprint
	 * @return the VS Blueprint element
	 */
	Optional<CtxBlueprint> findByNameAndVersion(String name, String version);
	
}
