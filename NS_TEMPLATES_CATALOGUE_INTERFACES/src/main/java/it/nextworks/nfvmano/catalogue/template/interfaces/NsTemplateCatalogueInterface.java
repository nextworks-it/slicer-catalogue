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

package it.nextworks.nfvmano.catalogue.template.interfaces;

import it.nextworks.nfvmano.catalogue.template.messages.OnBoardNsTemplateRequest;
import it.nextworks.nfvmano.catalogue.template.messages.QueryNsTemplateResponse;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;

public interface NsTemplateCatalogueInterface {
    /**
     * Method to create a new NS Template, including all the corresponding elements on the service orchestrator side.
     *
     * @param request NS Template creation request
     * @return the ID of the VS Blueprint
     * @throws MethodNotImplementedException if the method is not implemented
     * @throws MalformattedElementException if the request is malformatted
     * @throws AlreadyExistingEntityException if the NS Template already exists
     * @throws FailedOperationException if the operation fails
     */
    public String onBoardNsTemplate(OnBoardNsTemplateRequest request)
            throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException;

    /**
     * Method to retrieve a VS Blueprint from the catalogue.
     *
     * @param request query
     * @return the NS Template info
     * @throws MethodNotImplementedException if the method is not implemented
     * @throws MalformattedElementException if the request is malformatted
     * @throws NotExistingEntityException if the NS Template does not exist
     * @throws FailedOperationException if the operation fails
     */
    public QueryNsTemplateResponse queryNsTemplate(GeneralizedQueryRequest request)
            throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;

    /**
     * Method to delete a NS Template from the catalogue
     *
     * @param nsTemplateId ID of the NS template to be removed
     * @throws MethodNotImplementedException if the method is not implemented
     * @throws MalformattedElementException if the ID is malformatted
     * @throws NotExistingEntityException if the NS Template does not exist
     * @throws FailedOperationException if the operation fails
     */
    public void deleteNsTemplate(String nsTemplateId)
            throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException;

}
