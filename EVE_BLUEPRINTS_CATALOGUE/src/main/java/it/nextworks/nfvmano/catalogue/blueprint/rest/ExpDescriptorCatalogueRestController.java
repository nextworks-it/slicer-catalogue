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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.nfvmano.catalogue.blueprint.EveportalCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardExpDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryExpDescriptorResponse;
import it.nextworks.nfvmano.catalogue.blueprint.services.ExpDescriptorCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.sebastian.admin.MgmtCatalogueUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "Experiment Descriptor Catalogue API")
@RestController
@CrossOrigin
@RequestMapping("/portal/catalogue")
public class ExpDescriptorCatalogueRestController {

	private static final Logger log = LoggerFactory.getLogger(ExpDescriptorCatalogueRestController.class);
	
	@Autowired
	private ExpDescriptorCatalogueService expDescriptorCatalogueService;
	
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
	public ExpDescriptorCatalogueRestController() { } 
	
	@ApiOperation(value = "Onboard Experiment Descriptor")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Element created. Returns the id of the element created.", response = String.class),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 409, message = "There is a conflict with the request", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

	})
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "/expdescriptor", method = RequestMethod.POST)
	public ResponseEntity<?> createExpDescriptor(@RequestBody OnboardExpDescriptorRequest request, Authentication auth) {
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = getUserFromAuth(auth);
			if (!request.getTenantId().equals(user)) {
				return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
			}
			try {
				String expDescriptorId = expDescriptorCatalogueService.onboardExpDescriptor(request);
				return new ResponseEntity<String>(expDescriptorId, HttpStatus.CREATED);
			} catch (MalformattedElementException e) {
				log.error("Malformatted request");
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
			} catch (AlreadyExistingEntityException e) {
				log.error("EXP Descriptor already existing");
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
			} catch (Exception e) {
				log.error("Internal exception");
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}


	}
	
	
	
	@RequestMapping(value = "/expdescriptor", method = RequestMethod.GET)
	public ResponseEntity<?> getAllExpDescriptors(Authentication auth) {
		log.debug("Received request to retrieve all the EXP descriptors.");
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = getUserFromAuth(auth);
		try {

			QueryExpDescriptorResponse response = expDescriptorCatalogueService.queryExpDescriptor(
					new GeneralizedQueryRequest(MgmtCatalogueUtilities.buildTenantFilter(user), null)
			);
			return new ResponseEntity<List<ExpDescriptor>>(response.getExpDescriptors(), HttpStatus.OK);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("EXP Blueprints not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/expdescriptor/{expdId}", method = RequestMethod.GET)
	public ResponseEntity<?> getExpDescriptor(@PathVariable String expdId, Authentication auth) {
		log.debug("Received request to retrieve EXP descriptor with ID " + expdId);
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = getUserFromAuth(auth);
		try {

			QueryExpDescriptorResponse response = expDescriptorCatalogueService.queryExpDescriptor(
					new GeneralizedQueryRequest(
							EveportalCatalogueUtilities.buildExpDescriptorFilter(expdId, user),
							null
					)
			);
			return new ResponseEntity<ExpDescriptor>(response.getExpDescriptors().get(0), HttpStatus.OK);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("EXP Blueprints not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/expdescriptor/{expdId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteExpDescriptor(@PathVariable String expdId, Authentication auth) {
		log.debug("Received request to delete EXP descriptor with ID " + expdId);
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = getUserFromAuth(auth);
		try {

			expDescriptorCatalogueService.deleteExpDescriptor(expdId, user);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("EXP Blueprints not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
}
