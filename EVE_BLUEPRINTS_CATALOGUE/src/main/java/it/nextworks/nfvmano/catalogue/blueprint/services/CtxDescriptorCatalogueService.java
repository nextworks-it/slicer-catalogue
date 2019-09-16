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
package it.nextworks.nfvmano.catalogue.blueprint.services;


import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.CtxDescriptorCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryCtxDescriptorResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxDescriptorRepository;
import it.nextworks.nfvmano.libs.common.elements.Filter;
import it.nextworks.nfvmano.libs.common.exceptions.*;
import it.nextworks.nfvmano.libs.common.messages.GeneralizedQueryRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class CtxDescriptorCatalogueService implements CtxDescriptorCatalogueInterface {

	private static final Logger log = LoggerFactory.getLogger(CtxDescriptorCatalogueService.class);
	
	@Autowired
	private CtxDescriptorRepository ctxDescriptorRepository;
	
	@Autowired
	private CtxBlueprintInfoRepository ctxBlueprintRepository;
	
	@Autowired
	private CtxBlueprintCatalogueService ctxBlueprintCatalogueService;
	
	@Value("${catalogue.admin}")
	private String adminTenant;
	
    @Override
    public String onboardCtxDescriptor(OnboardCtxDescriptorRequest request) 
    		throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
    	
    	log.debug("Processing request to on-board a new context descriptor");
		request.isValid();
		
		if (!(ctxBlueprintRepository.findByCtxBlueprintId(request.getCtxd().getCtxBlueprintId()).isPresent())) {
			log.error("Context blueprint with ID " + request.getCtxd().getCtxBlueprintId() + " not found in DB. Impossible to add associated CTXD.");
			throw new FailedOperationException("Context blueprint with ID " + request.getCtxd().getCtxBlueprintId() + " not found in DB. Impossible to add associated CTXD.");
		}
		
		CtxDescriptor ctxd = new CtxDescriptor(request.getCtxd().getName(), 
				request.getCtxd().getVersion(), 
				request.getCtxd().getCtxBlueprintId(), 
				request.getCtxd().getCtxParameters(), 
				request.isPublic(), 
				request.getTenantId());
		
		
		String ctxdId = storeCtxd(ctxd);
		try {
			ctxBlueprintCatalogueService.addCtxdInBlueprint(ctxd.getCtxBlueprintId(), ctxdId);
		} catch (NotExistingEntityException e) {
			throw new FailedOperationException(e.getMessage());
		}
		return ctxdId;
    }

    @Override
    public QueryCtxDescriptorResponse queryCtxDescriptor(GeneralizedQueryRequest request) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
    	log.debug("Processing a query for a CTX descriptor");
		request.isValid();

		//At the moment the only filters accepted are:
		//1. CTX Descriptor ID and tenant ID
		//CTXD_ID & TENANT_ID
		//2. Tenant ID
		//TENANT_ID
		//No attribute selector is supported at the moment
		
		List<CtxDescriptor> ctxDescriptors = new ArrayList<>();
		Filter filter = request.getFilter();
        List<String> attributeSelector = request.getAttributeSelector();
        if ((attributeSelector == null) || (attributeSelector.isEmpty())) {
        	Map<String, String> fp = filter.getParameters();
            if (fp.size() == 1 && fp.containsKey("TENANT_ID")) {
            	String tenantId = fp.get("TENANT_ID");
            	if (tenantId.equals(adminTenant)) {
            		log.debug("CTXD query for admin: returning all the CTXDs");
            		ctxDescriptors = ctxDescriptorRepository.findAll();
            		log.debug("Retrieved all the CTXDs");
            	} else {
            		ctxDescriptors = ctxDescriptorRepository.findByTenantId(tenantId);
            		log.debug("Added CTXDs for tenant " + tenantId);
            		List<CtxDescriptor> tmpPublicCtxds = ctxDescriptorRepository.findByIsPublic(true);
            		for (CtxDescriptor ctxd : tmpPublicCtxds) {
            			if (!(ctxd.getTenantId().equals(tenantId))) {
            				ctxDescriptors.add(ctxd);
            				log.debug("Added public CTXD " + ctxd.getName());
            			}
            		}
            	}
            } else if (fp.size() == 2 && fp.containsKey("CTXD_ID") && fp.containsKey("TENANT_ID")) {
            	String ctxdId = fp.get("CTXD_ID");
            	String tenantId = fp.get("TENANT_ID");
            	Optional<CtxDescriptor> ctxd = null;
            	if (tenantId.equals(adminTenant)) {
            		ctxd = ctxDescriptorRepository.findByCtxDescriptorId(ctxdId);
            	} else {
            		ctxd = ctxDescriptorRepository.findByCtxDescriptorIdAndTenantId(ctxdId, tenantId);
            	}
            	if (ctxd.isPresent()) {
            		ctxDescriptors.add(ctxd.get());
            		log.debug("Added CTXD with ID " + ctxdId + " for tenant " + tenantId);
            	} else throw new NotExistingEntityException("CTXD with CTXD ID " + ctxdId + " for tenant " + tenantId + " not found");
            } else if (fp.isEmpty()) {
            	ctxDescriptors = ctxDescriptorRepository.findByIsPublic(true);
            	log.debug("Added all the public CTXD available in DB.");
            }
            return new QueryCtxDescriptorResponse(ctxDescriptors);
        } else {
            log.error("Received query CTX Descriptor with attribute selector. Not supported at the moment.");
            throw new MethodNotImplementedException("Received query CTX Descriptor with attribute selector. Not supported at the moment.");
        }
    }

    @Override
    public void deleteCtxDescriptor(String ctxDescriptorId, String tenantId) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
    	log.debug("Processing request to delete a CTX descriptor");
		if  (ctxDescriptorId == null) throw new MalformattedElementException("CTXD ID is null");
		
		Optional<CtxDescriptor> ctxdOpt = ctxDescriptorRepository.findByCtxDescriptorId(ctxDescriptorId);
		if (ctxdOpt.isPresent()) {
			CtxDescriptor ctxd = ctxdOpt.get();
			if ( (ctxd.getTenantId().equals(tenantId)) || (tenantId.equals(adminTenant)) ) {
				String ctxbId = ctxd.getCtxBlueprintId();
				ctxDescriptorRepository.delete(ctxd);
				ctxBlueprintCatalogueService.removeCtxdInBlueprint(ctxbId, ctxDescriptorId);
				log.debug("CTXD " + ctxDescriptorId + " removed from the internal DB.");
			} else {
				log.error("Tenant " + tenantId + " does not have the right to remove the CTXD " + ctxDescriptorId);
				throw new FailedOperationException("Tenant " + tenantId + " does not have the right to remove the CTXD " + ctxDescriptorId);
			}
		} else {
			log.error("CTXD " + ctxDescriptorId + " not found");
			throw new NotExistingEntityException("CTXD " + ctxDescriptorId + " not found");
		}
    }
    
    private String storeCtxd(CtxDescriptor ctxd) 
    		throws AlreadyExistingEntityException, FailedOperationException {
		log.debug("On boarding Context Descriptor with name " + ctxd.getName() + " and version " + ctxd.getVersion() + " for tenant " + ctxd.getTenantId());
		
		ctxDescriptorRepository.saveAndFlush(ctxd);
		String ctxdId = String.valueOf(ctxd.getId());
		ctxd.setCtxDescriptorId(ctxdId);
		ctxDescriptorRepository.saveAndFlush(ctxd);
		log.debug("Added Context Descriptor with ID " + ctxdId);
		return ctxdId;
	}

}
