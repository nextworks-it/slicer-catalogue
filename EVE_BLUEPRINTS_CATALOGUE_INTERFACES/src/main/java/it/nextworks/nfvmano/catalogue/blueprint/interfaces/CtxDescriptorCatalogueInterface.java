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

import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryCtxDescriptorResponse;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;

public interface CtxDescriptorCatalogueInterface {

	/**
	 * Method to create a new CtxD
	 * 
	 * @param request
	 * @return
	 * @throws MethodNotImplementedException
	 * @throws MalformattedElementException
	 * @throws AlreadyExistingEntityException
	 * @throws FailedOperationException
	 */
	public String onboardCtxDescriptor(OnboardCtxDescriptorRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException;
	
	/**
	 * Method to request info about an existing CtxD
	 * 
	 * @param request
	 * @return
	 * @throws MethodNotImplementedException
	 * @throws MalformattedElementException
	 * @throws NotExistingEntityException
	 * @throws FailedOperationException
	 */
	public QueryCtxDescriptorResponse queryCtxDescriptor(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;
	
	/**
	 * Method to remove a CtxD
	 * 
	 * @param ctxDescriptorId
	 * @param tenantId
	 * @throws MethodNotImplementedException
	 * @throws MalformattedElementException
	 * @throws NotExistingEntityException
	 * @throws FailedOperationException
	 */
	public void deleteCtxDescriptor(String ctxDescriptorId, String tenantId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;

}
