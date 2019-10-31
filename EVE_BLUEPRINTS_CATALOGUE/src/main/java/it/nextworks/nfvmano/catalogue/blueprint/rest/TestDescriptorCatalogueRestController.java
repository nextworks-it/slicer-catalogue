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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.nfvmano.catalogue.blueprint.elements.TestCaseDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryTestCaseDescriptorResponse;
import it.nextworks.nfvmano.catalogue.blueprint.services.TcDescriptorCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.sebastian.admin.MgmtCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.EveportalCatalogueUtilities;

@Api(tags = "Test Case Descriptor Catalogue API")
@RestController
@CrossOrigin
@RequestMapping("/portal/catalogue")
public class TestDescriptorCatalogueRestController {

	private static final Logger log = LoggerFactory.getLogger(TestDescriptorCatalogueRestController.class);
	
	@Autowired
	private TcDescriptorCatalogueService tcDescriptorCatalogueService;
	
	@Value("${catalogue.admin}")
	private String adminTenant;

	@Value("${authentication.enable}")
	private boolean authenticationEnable;

	private  String getUserFromAuth(Authentication auth) {
		if(authenticationEnable){
			Object principal = auth.getPrincipal();
			if (!UserDetails.class.isAssignableFrom(principal.getClass())) {
				throw new IllegalArgumentException("Auth.getPrincipal() does not implement UserDetails");
			}
			return ((UserDetails) principal).getUsername();
		}else return adminTenant;

	}

	private  boolean validateAuthentication(Authentication auth){
		return !authenticationEnable || auth!=null;

	}
	
	public TestDescriptorCatalogueRestController() { }
	
	//Note: only GET requests are supported by the REST controller, since create and delete actions are managed through the creation and deletion of the associated experiment descriptor
	
	@ApiOperation(value = "Query ALL the Test Case Descriptors")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "List of all the Test Case Descriptors of the user.", response = TestCaseDescriptor.class, responseContainer = "Set"),
	})
	@RequestMapping(value = "/testcasedescriptor", method = RequestMethod.GET)
	public ResponseEntity<?> getAllTestCaseDescriptors(Authentication auth) {
		log.debug("Received request to retrieve all the TC descriptors.");
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = getUserFromAuth(auth);
		try {

			QueryTestCaseDescriptorResponse response = tcDescriptorCatalogueService.queryTestCaseDescriptor(
					new GeneralizedQueryRequest(MgmtCatalogueUtilities.buildTenantFilter(user), null)
			);
			return new ResponseEntity<List<TestCaseDescriptor>>(response.getTestCaseDescriptors(), HttpStatus.OK);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("TC Descriptors not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation(value = "Query a Test Case Descriptor with a given ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message =  "Details of the Test Case Descriptor with the given ID", response = TestCaseDescriptor.class),
	})
	@RequestMapping(value = "/testcasedescriptor/{tcdId}", method = RequestMethod.GET)
	public ResponseEntity<?> getTcDescriptor(@PathVariable String tcdId, Authentication auth) {
		log.debug("Received request to retrieve Test case descriptor with ID " + tcdId);
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = getUserFromAuth(auth);
		try {

			QueryTestCaseDescriptorResponse response = tcDescriptorCatalogueService.queryTestCaseDescriptor(
					new GeneralizedQueryRequest(
							EveportalCatalogueUtilities.buildTestCaseDescriptorFilterFromId(tcdId, user),
							null
					)
			);
			return new ResponseEntity<TestCaseDescriptor>(response.getTestCaseDescriptors().get(0), HttpStatus.OK);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("TC Descriptor not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

}
