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

import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardExpDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryExpDescriptorResponse;
import it.nextworks.nfvmano.libs.common.exceptions.*;
import it.nextworks.nfvmano.libs.common.messages.GeneralizedQueryRequest;

import java.util.Optional;

public interface ExpDescriptorCatalogueInterface {

	/**
	 * Method to create a new EXPD
	 * 
	 * @param request
	 * @return
	 * @throws MethodNotImplementedException
	 * @throws MalformattedElementException
	 * @throws AlreadyExistingEntityException
	 * @throws FailedOperationException
	 */
	public String onboardExpDescriptor(OnboardExpDescriptorRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException;
	
	/**
	 * Method to request info about an existing ExpD
	 * 
	 * @param request
	 * @return
	 * @throws MethodNotImplementedException
	 * @throws MalformattedElementException
	 * @throws NotExistingEntityException
	 * @throws FailedOperationException
	 */
	public QueryExpDescriptorResponse queryExpDescriptor(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;
	
	/**
	 * Method to remove a ExpD
	 * 
	 * @param expDescriptorId
	 * @param tenantId
	 * @throws MethodNotImplementedException
	 * @throws MalformattedElementException
	 * @throws NotExistingEntityException
	 * @throws FailedOperationException
	 */
	public void deleteExpDescriptor(String expDescriptorId, String tenantId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;


	/**
	 * Method to retrieve a EXPD
	 *
	 * @param expDescriptorId
	 * @return the EXP Descriptor Element
	 */
	public Optional<ExpDescriptor> findByExpDescriptorId(String expDescriptorId);
}
