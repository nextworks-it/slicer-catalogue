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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.nextworks.nfvmano.catalogue.blueprint.elements.TestCaseDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.TestCaseDescriptorCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardTestCaseDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryTestCaseDescriptorResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TestCaseBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TestCaseDescriptorRepository;
import it.nextworks.nfvmano.libs.common.elements.Filter;
import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.common.messages.GeneralizedQueryRequest;

@Service
public class TcDescriptorCatalogueService implements TestCaseDescriptorCatalogueInterface {

	private static final Logger log = LoggerFactory.getLogger(TcDescriptorCatalogueService.class);
	
	@Autowired
	private TestCaseDescriptorRepository testCaseDescriptorRepository;
	
	@Autowired
	private TestCaseBlueprintRepository testCaseBlueprintRepository;
	
	@Value("${catalogue.admin}")
	private String adminTenant;
	
	public TcDescriptorCatalogueService() {	}

	@Override
	public synchronized String onboardTestCaseDescriptor(OnboardTestCaseDescriptorRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException,
			FailedOperationException {
		log.debug("Processing request to on-board a new test case descriptor");
		request.isValid();
		
		if (!(testCaseBlueprintRepository.findByTestcaseBlueprintId(request.getTestCaseDescriptor().getTestCaseBlueprintId()).isPresent())) {
			log.error("Test case blueprint with ID " + request.getTestCaseDescriptor().getTestCaseBlueprintId() + " not found in DB. Impossible to add associated test case descriptor.");
			throw new FailedOperationException("Test case blueprint with ID " + request.getTestCaseDescriptor().getTestCaseBlueprintId() + " not found in DB. Impossible to add associated test case descriptor.");
		}
		
		//note: test case descriptors are always onboarded as part of experiment descriptor, so the user authZ processing is implemented at the expD onboarding level
		//here we are assuming the request is authorized
		TestCaseDescriptor tcd = new TestCaseDescriptor(null,
				request.getTestCaseDescriptor().getName(),
				request.getTestCaseDescriptor().getVersion(),
				request.getTestCaseDescriptor().getTestCaseBlueprintId(),
				request.getTestCaseDescriptor().getUserParameters(),
				request.isPublic(), 
				request.getTenantId());
		
		String tcdId = storeTcd(tcd);
		
		return tcdId;
	}

	@Override
	public synchronized QueryTestCaseDescriptorResponse queryTestCaseDescriptor(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException,
			FailedOperationException {
		log.debug("Processing a query for a test case descriptor");
		request.isValid();

		//At the moment the only filters accepted are:
		//1. Test Case Descriptor ID and Tenant ID
		//TCD_ID & TENANT_ID
		//No attribute selector is supported at the moment
		
		List<TestCaseDescriptor> tcDescriptors = new ArrayList<>();
		Filter filter = request.getFilter();
        List<String> attributeSelector = request.getAttributeSelector();
        if ((attributeSelector == null) || (attributeSelector.isEmpty())) {
        	Map<String, String> fp = filter.getParameters();
            if (fp.size() == 2 && fp.containsKey("TCD_ID") && fp.containsKey("TENANT_ID")) {
            	String tcdId = fp.get("TCD_ID");
            	String tenantId = fp.get("TENANT_ID");
            	Optional<TestCaseDescriptor> tcdOpt = testCaseDescriptorRepository.findByTestCaseDescriptorIdAndTenantId(tcdId, tenantId);
            	if (!(tcdOpt.isPresent())) {
            		log.error("Test case descriptor with ID " + tcdId + " not found in DB.");
            		throw new NotExistingEntityException("Test case descriptor with ID " + tcdId + " not found in DB.");
            	} else {
            		tcDescriptors.add(tcdOpt.get());
            		log.debug("Added TCD with ID " + tcdId);
            	}
            } else if (fp.isEmpty()) {
            	tcDescriptors = testCaseDescriptorRepository.findAll();
            	log.debug("Added all TCDs");
            }
            return new QueryTestCaseDescriptorResponse(tcDescriptors);	
        } else {
            log.error("Received query Test Case Descriptor with attribute selector. Not supported at the moment.");
            throw new MethodNotImplementedException("Received query Test Case Descriptor with attribute selector. Not supported at the moment.");
        }
	}

	@Override
	public synchronized void deleteTestCaseDescriptor(String testcaseDescriptorId, String tenantId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException,
			FailedOperationException {
		log.debug("Processing request to delete a test case descriptor");
		if  (testcaseDescriptorId == null) throw new MalformattedElementException("TCD ID is null");
		
		Optional<TestCaseDescriptor> tcdOpt = testCaseDescriptorRepository.findByTestCaseDescriptorId(testcaseDescriptorId);
		if (tcdOpt.isPresent()) {
			testCaseDescriptorRepository.delete(tcdOpt.get());
			log.debug("TCD " + testcaseDescriptorId + " removed from the internal DB.");
		} else {
			log.error("TCD " + testcaseDescriptorId + " not found");
			throw new NotExistingEntityException("TCD " + testcaseDescriptorId + " not found");
		}
	}
	
	private String storeTcd(TestCaseDescriptor tcd)
			throws AlreadyExistingEntityException, FailedOperationException {
		log.debug("On boarding Test Case Descriptor with name " + tcd.getName() + " and version " + tcd.getVersion());
		testCaseDescriptorRepository.saveAndFlush(tcd);
		String tcdId = String.valueOf(tcd.getId());
		tcd.setTestCaseDescriptorId(tcdId);
		testCaseDescriptorRepository.saveAndFlush(tcd);
		log.debug("Added Test Case Descriptor with ID " + tcdId);
		return tcdId;
	}

}
