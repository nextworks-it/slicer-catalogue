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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import it.nextworks.nfvmano.catalogue.blueprint.BlueprintCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.services.AuthService;
import it.nextworks.nfvmano.sebastian.admin.MgmtCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.services.VsDescriptorCatalogueService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardVsDescriptorRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryVsDescriptorResponse;

@Api(tags = "Vertical Service Descriptor Catalogue API")

@RestController
@CrossOrigin
@RequestMapping("/portal/catalogue")
public class VsDescriptorCatalogueRestController {

	private static final Logger log = LoggerFactory.getLogger(VsDescriptorCatalogueRestController.class);
	
	@Autowired
	private VsDescriptorCatalogueService vsDescriptorCatalogueService;
	
	@Value("${catalogue.admin}")
	private String adminTenant;

	@Value("${authentication.enable}")
	private boolean authenticationEnable;

	

	@Autowired
	private AuthService authService;

	
	public VsDescriptorCatalogueRestController() { }


	@ApiOperation(value = "Onboard a new Vertical Service Descriptor")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "The ID of the created Vertical Service Descriptor.", response = String.class),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 409, message = "There is a conflict with the request", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

	})


	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "/vsdescriptor", method = RequestMethod.POST)
	public ResponseEntity<?> createVsDescriptor(@RequestBody OnboardVsDescriptorRequest request, Authentication auth) {
		log.debug("Received request to create a VS descriptor.");
		String user = authService.getUserFromAuth(auth);
		if (!request.getTenantId().equals(user)) {
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		try {
			String vsDescriptorId = vsDescriptorCatalogueService.onBoardVsDescriptor(request);
			return new ResponseEntity<String>(vsDescriptorId, HttpStatus.CREATED);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (AlreadyExistingEntityException e) {
			log.error("VS Blueprint already existing");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			log.error("Internal exception", e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@ApiOperation(value = "Query ALL the Vertical Service Descriptor")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "List of all the Vertical Service Descriptor of the user.", response = VsDescriptor.class, responseContainer = "Set"),
	})
	@RequestMapping(value = "/vsdescriptor", method = RequestMethod.GET)
	public ResponseEntity<?> getAllVsDescriptors(Authentication auth) {
		log.debug("Received request to retrieve all the VS descriptors.");
		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		try {
			String user = authService.getUserFromAuth(auth);
			QueryVsDescriptorResponse response = vsDescriptorCatalogueService.queryVsDescriptor(
					new GeneralizedQueryRequest(MgmtCatalogueUtilities.buildTenantFilter(user), null)
			);
			return new ResponseEntity<List<VsDescriptor>>(response.getVsDescriptors(), HttpStatus.OK);
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
	
	@ApiOperation(value = "Query a Vertical Service Descriptor with a given ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message =  "Details of the Vertical Service Descriptor with the given ID", response = VsDescriptor.class),
			//@ApiResponse(code = 400, message = "The supplied element contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)
	})
	@RequestMapping(value = "/vsdescriptor/{vsdId}", method = RequestMethod.GET)
	public ResponseEntity<?> getVsDescriptor(@PathVariable String vsdId, Authentication auth) {
		log.debug("Received request to retrieve VS descriptor with ID " + vsdId);
		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		try {
			String user = authService.getUserFromAuth(auth);
			QueryVsDescriptorResponse response = vsDescriptorCatalogueService.queryVsDescriptor(
					new GeneralizedQueryRequest(
							BlueprintCatalogueUtilities.buildVsDescriptorFilter(vsdId, user),
							null
					)
			);
			return new ResponseEntity<VsDescriptor>(response.getVsDescriptors().get(0), HttpStatus.OK);
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
	

	@ApiOperation(value = "Delete a Vertical Service Descriptor with the given ID")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Empty", response = ResponseEntity.class),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)
	})
	@RequestMapping(value = "/vsdescriptor/{vsdId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteVsDescriptor(@PathVariable String vsdId, Authentication auth) {
		log.debug("Received request to delete VS descriptor with ID " + vsdId);
		try {
			String user = authService.getUserFromAuth(auth);
			vsDescriptorCatalogueService.deleteVsDescriptor(vsdId, user);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

	

	
}
