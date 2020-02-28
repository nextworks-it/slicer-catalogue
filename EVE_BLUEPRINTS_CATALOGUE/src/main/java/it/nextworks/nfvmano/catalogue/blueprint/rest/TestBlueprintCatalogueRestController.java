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
package it.nextworks.nfvmano.catalogue.blueprint.rest;

import java.util.List;

import it.nextworks.nfvmano.catalogue.blueprint.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.nfvmano.catalogue.blueprint.EveportalCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.elements.TestCaseBlueprintInfo;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardTestCaseBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryTestCaseBlueprintResponse;
import it.nextworks.nfvmano.catalogue.blueprint.services.TcBlueprintCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;

@Api(tags = "Test Case Blueprint Catalogue API")
@RestController
@CrossOrigin
@RequestMapping("/portal/catalogue")
public class TestBlueprintCatalogueRestController {
	
	private static final Logger log = LoggerFactory.getLogger(TestBlueprintCatalogueRestController.class);
	
	@Value("${catalogue.admin}")
	private String adminTenant;
	
	@Autowired
	private TcBlueprintCatalogueService tcBlueprintCatalogueService;


	@Value("${keycloak.enabled}")
	private boolean keycloakEnabled;


	@Autowired
	private AuthService authService;


	public TestBlueprintCatalogueRestController() {	}
	
	@ApiOperation(value = "Onboard a new Test Case Blueprint.")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "The ID of the created Test Case Blueprint.", response = String.class),
	})
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "/testcaseblueprint", method = RequestMethod.POST)
	public ResponseEntity<?> createTestCaseBlueprint(@RequestBody OnboardTestCaseBlueprintRequest request, Authentication auth) {
		log.debug("Received request to create a Test case blueprint.");
		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = authService.getUserFromAuth(auth);
		if (!keycloakEnabled&&!user.equals(adminTenant)) {
			log.warn("Request refused as tenant {} is not admin.", user);
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		request.setOwner(user);
		try {
			String testCaseBlueprintId = tcBlueprintCatalogueService.onboardTestCaseBlueprint(request);
			return new ResponseEntity<String>(testCaseBlueprintId, HttpStatus.CREATED);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (AlreadyExistingEntityException e) {
			log.error("Test case Blueprint already existing");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation(value = "Get ALL the Test Case Service Blueprints")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "List of all the Test Case Service Blueprints of the user", response = TestCaseBlueprintInfo.class, responseContainer = "Set"),
	})
	@RequestMapping(value = "/testcaseblueprint", method = RequestMethod.GET)
	public ResponseEntity<?> getAllTestCaseBlueprints(Authentication auth) {
		log.debug("Received request to retrieve all the Test case blueprints.");
		//jb: disabled authentication control in order to allow certain hosts to retrieve information without
		//being authenticated
		/*
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}*/

		try {
			String user = authService.getUserFromAuth(auth);
			QueryTestCaseBlueprintResponse response = tcBlueprintCatalogueService.queryTestCaseBlueprint(
					new GeneralizedQueryRequest(EveportalCatalogueUtilities.buildTenantFilter(user), null)
					); 
			return new ResponseEntity<List<TestCaseBlueprintInfo>>(response.getTestCaseBlueprints(), HttpStatus.OK);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("VS Blueprints not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation(value = "Get a Test Case Blueprint with a given ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Test Case Blueprint with the given ID", response = TestCaseBlueprintInfo.class),
	})
	@RequestMapping(value = "/testcaseblueprint/{tcbId}", method = RequestMethod.GET)
	public ResponseEntity<?> getTcBlueprint(@PathVariable String tcbId, Authentication auth) {
		log.debug("Received request to retrieve test case blueprint with ID " + tcbId);
		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}

		try {
			String user = authService.getUserFromAuth(auth);
			QueryTestCaseBlueprintResponse response = tcBlueprintCatalogueService.queryTestCaseBlueprint(
					new GeneralizedQueryRequest(EveportalCatalogueUtilities.buildTestCaseBlueprintFilterFromIdAndTenant(tcbId, user), null)
					);
			return new ResponseEntity<TestCaseBlueprintInfo>(response.getTestCaseBlueprints().get(0), HttpStatus.OK);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("Test case Blueprints not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation(value = "Delete a Test Case Blueprint with a given ID")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Empty", response = ResponseEntity.class),
	})
	@RequestMapping(value = "/testcaseblueprint/{tcbId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteTestCaseBlueprint(@PathVariable String tcbId, Authentication auth) {
		log.debug("Received request to delete Test case blueprint with ID " + tcbId);
		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = authService.getUserFromAuth(auth);
		if (!keycloakEnabled&&!user.equals(adminTenant)) {
			log.warn("Request refused as tenant {} is not admin.", user);
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}

		try {
			tcBlueprintCatalogueService.deleteTestCaseBlueprint(tcbId); 
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("Test case Blueprints not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



}
