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
import org.springframework.stereotype.Service;

import it.nextworks.nfvmano.catalogue.blueprint.elements.TestCaseBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.TestCaseBlueprintInfo;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.TestCaseBlueprintCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardTestCaseBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryTestCaseBlueprintResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TestCaseBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TestCaseBlueprintRepository;
import it.nextworks.nfvmano.libs.common.elements.Filter;
import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.common.messages.GeneralizedQueryRequest;

@Service
public class TcBlueprintCatalogueService implements TestCaseBlueprintCatalogueInterface {

	private static final Logger log = LoggerFactory.getLogger(TcBlueprintCatalogueService.class);
	
	@Autowired
	private TestCaseBlueprintRepository testCaseBlueprintRepository;
	
	@Autowired
	private TestCaseBlueprintInfoRepository testCaseBlueprintInfoRepository;
	
	public TcBlueprintCatalogueService() {	}

	@Override
	public synchronized String onboardTestCaseBlueprint(OnboardTestCaseBlueprintRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
		log.debug("Process Test Case Blueprint Onboard request");
		request.isValid();
		
		TestCaseBlueprint tcb = request.getTestCaseBlueprint();
		String testCaseBlueprintId = storeTestCaseBlueprint(tcb);
		return testCaseBlueprintId;
	}

	@Override
	public QueryTestCaseBlueprintResponse queryTestCaseBlueprint(GeneralizedQueryRequest request)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		log.debug("Processing request to query a test case blueprint");
		request.isValid();
		
		//At the moment the only filters accepted are:
		//1. Test Case Blueprint name and version
		//TCB_NAME & TCB_VERSION
		//2. Test Case Blueprint ID
		//TCB_ID
        //No attribute selector is supported at the moment
		
		List<TestCaseBlueprintInfo> tcbInfos = new ArrayList<>();
		Filter filter = request.getFilter();
        List<String> attributeSelector = request.getAttributeSelector();
        if ((attributeSelector == null) || (attributeSelector.isEmpty())) {
        	Map<String, String> fp = filter.getParameters();
        	if (fp.size() == 1 && fp.containsKey("TCB_ID")) {
        		String tcbId = fp.get("TCB_ID");
        		TestCaseBlueprintInfo tcbi = getTestCaseBlueprintInfo(tcbId);
        		tcbInfos.add(tcbi);
            	log.debug("Added Test Case Blueprint info for TCB ID " + tcbId);
        	} else if (fp.size() == 2 && fp.containsKey("TCB_NAME") && fp.containsKey("TCB_VERSION")) {
        		String tcbName = fp.get("TCB_NAME");
            	String tcbVersion = fp.get("TCB_VERSION");
            	TestCaseBlueprintInfo tcbi = getTestCaseBlueprintInfo(tcbName, tcbVersion);
            	tcbInfos.add(tcbi);
            	log.debug("Added Test Case Blueprint info for TCB with name " + tcbName + " and version " + tcbVersion);
        	} else if (fp.isEmpty()) {
        		tcbInfos = getAllTestCaseBlueprintInfos();
            	log.debug("Addes all the VSB info available in DB.");
        	}
        	return new QueryTestCaseBlueprintResponse(tcbInfos);
        } else {
            log.error("Received query Test Case Blueprint with attribute selector. Not supported at the moment.");
            throw new MethodNotImplementedException("Received query Test Case Blueprint with attribute selector. Not supported at the moment.");
        }
	}

	@Override
	public void deleteTestCaseBlueprint(String testCaseBlueprintId)
			throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		
		log.debug("Processing request to delete a test case blueprint with ID " + testCaseBlueprintId);
		
		if (testCaseBlueprintId == null) throw new MalformattedElementException("The test case blueprint ID is null");
		
		TestCaseBlueprintInfo tcbi = getTestCaseBlueprintInfo(testCaseBlueprintId);
		
		if (!(tcbi.getActiveTcdId().isEmpty())) {
			log.error("There are some test case descriptors associated to the Test Case Blueprint. Impossible to remove it.");
			throw new FailedOperationException("There are some test case descriptors associated to the Test Case Blueprint. Impossible to remove it.");
		}
		
		testCaseBlueprintInfoRepository.delete(tcbi.getId());
		log.debug("Removed test case blueprint info from DB.");
		TestCaseBlueprint tcb = getTestCaseBlueprint(testCaseBlueprintId);
		testCaseBlueprintRepository.delete(tcb);
		log.debug("Removed test case blueprint from DB.");
	}
	
	private TestCaseBlueprint getTestCaseBlueprint(String blueprintId) throws NotExistingEntityException {
		Optional<TestCaseBlueprint> testCaseBlueprintOpt = testCaseBlueprintRepository.findByTestcaseBlueprintId(blueprintId);
		if (testCaseBlueprintOpt.isPresent()) return testCaseBlueprintOpt.get();
		else throw new NotExistingEntityException("Test Case Blueprint with ID " + blueprintId + " not found in DB.");
	}
	
	private TestCaseBlueprint getTestCaseBlueprint(String name, String version) throws NotExistingEntityException {
		Optional<TestCaseBlueprint> testCaseBlueprintOpt = testCaseBlueprintRepository.findByNameAndVersion(name, version);
		if (testCaseBlueprintOpt.isPresent()) return testCaseBlueprintOpt.get();
		else throw new NotExistingEntityException("Test Case Blueprint with name " + name + " and version " + version + " not found in DB.");
	}
	
	private TestCaseBlueprintInfo getTestCaseBlueprintInfo(String blueprintId) throws NotExistingEntityException {
		TestCaseBlueprintInfo testCaseBlueprintInfo;
		Optional<TestCaseBlueprintInfo> testCaseBlueprintInfoOpt = testCaseBlueprintInfoRepository.findByTestCaseBlueprintId(blueprintId);
		if (testCaseBlueprintInfoOpt.isPresent()) testCaseBlueprintInfo = testCaseBlueprintInfoOpt.get();
		else throw new NotExistingEntityException("Test Case Blueprint info for test case blueprint with ID " + blueprintId + " not found in DB.");
		TestCaseBlueprint tcb = getTestCaseBlueprint(blueprintId);
		testCaseBlueprintInfo.setTestCaseBlueprint(tcb);
		return testCaseBlueprintInfo;
	}
	
	private TestCaseBlueprintInfo getTestCaseBlueprintInfo(String name, String version) throws NotExistingEntityException {
		TestCaseBlueprintInfo testCaseBlueprintInfo;
		Optional<TestCaseBlueprintInfo> testCaseBlueprintInfoOpt = testCaseBlueprintInfoRepository.findByNameAndVersion(name, version);
		if (testCaseBlueprintInfoOpt.isPresent()) testCaseBlueprintInfo = testCaseBlueprintInfoOpt.get();
		else throw new NotExistingEntityException("Test Case Blueprint info for test case blueprint with name " + name + " and version " + version + " not found in DB.");
		TestCaseBlueprint tcb = getTestCaseBlueprint(name, version);
		testCaseBlueprintInfo.setTestCaseBlueprint(tcb);
		return testCaseBlueprintInfo;
	}
	
	private List<TestCaseBlueprintInfo> getAllTestCaseBlueprintInfos() throws NotExistingEntityException {
		List<TestCaseBlueprintInfo> tcbis = testCaseBlueprintInfoRepository.findAll();
		for (TestCaseBlueprintInfo tcbi : tcbis) {
			String name = tcbi.getName();
			String version = tcbi.getVersion();
			TestCaseBlueprint tcb = getTestCaseBlueprint(name, version);
			tcbi.setTestCaseBlueprint(tcb);
		}
		return tcbis;
	}
	
	private String storeTestCaseBlueprint(TestCaseBlueprint tcb) throws AlreadyExistingEntityException {
		
		log.debug("Onboarding test case blueprint with name " + tcb.getName() + " and version " + tcb.getVersion());
		
		if ( (testCaseBlueprintInfoRepository.findByNameAndVersion(tcb.getName(), tcb.getVersion()).isPresent()) ||
			(testCaseBlueprintRepository.findByNameAndVersion(tcb.getName(), tcb.getVersion()).isPresent())) {
				log.error("Test Case Blueprint with name " + tcb.getName() + " and version " + tcb.getVersion() + " already present in DB.");
	            throw new AlreadyExistingEntityException("Test Case Blueprint with name " + tcb.getName() + " and version " + tcb.getVersion() + " already present in DB.");
		}
		
		TestCaseBlueprint target = new TestCaseBlueprint(null, 
				tcb.getName(), 
				tcb.getVersion(),
				tcb.getDescription(),
				tcb.getScript(), 
				tcb.getUserParameters(),
				tcb.getInfrastructureParameters());
		
		testCaseBlueprintRepository.saveAndFlush(target);
		Long tcbId = target.getId();
        String tcbIdString = String.valueOf(tcbId);
        target.setTestcaseBlueprintId(tcbIdString);
        testCaseBlueprintRepository.saveAndFlush(target);
        log.debug("Added Test Case Blueprint with ID " + tcbIdString);
        
        TestCaseBlueprintInfo tcbInfo = new TestCaseBlueprintInfo(tcbIdString, tcb.getVersion(), tcb.getName());
        testCaseBlueprintInfoRepository.saveAndFlush(tcbInfo);
        log.debug("Added Test Case Blueprint Info with ID " + tcbIdString);

        return tcbIdString;
	}

}
