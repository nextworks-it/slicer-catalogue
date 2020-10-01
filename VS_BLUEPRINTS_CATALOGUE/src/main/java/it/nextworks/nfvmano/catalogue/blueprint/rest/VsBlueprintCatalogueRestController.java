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
import it.nextworks.nfvmano.catalogue.blueprint.services.VsBlueprintCatalogueService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsBlueprintInfo;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnBoardVsBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryVsBlueprintResponse;

@Api(tags = "Vertical Service Blueprint Catalogue API")
@RestController
@CrossOrigin
@RequestMapping("/vs/catalogue")
public class VsBlueprintCatalogueRestController {

	private static final Logger log = LoggerFactory.getLogger(VsBlueprintCatalogueRestController.class);

	@Autowired
	private VsBlueprintCatalogueService vsBlueprintCatalogueService;

	@Value("${catalogue.admin}")
	private String adminTenant;

	@Value("${authentication.enable}")
	private boolean authenticationEnable;



	@Autowired
	private AuthService authService;

	public VsBlueprintCatalogueRestController() { }

	@ApiOperation(value = "Onboard a new Vertical Service Blueprint, including the associated Network Service Descriptors and translation rules.")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "The ID of the created Vertical Service Blueprint.", response = String.class),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 409, message = "There is a conflict with the request", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

	})
	@ResponseStatus(HttpStatus.CREATED)

	@RequestMapping(value = "/vsblueprint", method = RequestMethod.POST)
	public ResponseEntity<?> createVsBlueprint(@RequestBody OnBoardVsBlueprintRequest request, Authentication auth) {

		//SecurityConfig should only allow this REST call to certain roles if keycloack enabled
		log.debug("Received request to create a VS blueprint.");

		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = authService.getUserFromAuth(auth);
		if (!user.equals(adminTenant)) {
			log.warn("Request refused as tenant {} is not admin.", user);
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		try {
			String vsBlueprintId = vsBlueprintCatalogueService.onBoardVsBlueprint(request);
			return new ResponseEntity<String>(vsBlueprintId, HttpStatus.CREATED);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (AlreadyExistingEntityException e) {
			log.error("VS Blueprint already existing");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "Get ALL the Vertical Service Blueprints")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "List of all the Vertical Service Blueprints of the user", response = VsBlueprintInfo.class, responseContainer = "Set"),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)
	})
	@RequestMapping(value = "/vsblueprint", method = RequestMethod.GET)
	public ResponseEntity<?> getAllVsBlueprints(@RequestParam(required = false) String id, @RequestParam(required = false) String site, Authentication auth) {
		log.debug("Received request to retrieve all the VS blueprints.");
		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		try {
			String user = authService.getUserFromAuth(auth);
			if ((id == null) && (site == null)) {
				QueryVsBlueprintResponse response = vsBlueprintCatalogueService.queryVsBlueprint(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildTenantFilter(user), null));
				return new ResponseEntity<List<VsBlueprintInfo>>(response.getVsBlueprintInfo(), HttpStatus.OK);
			} else if (id != null) {
				QueryVsBlueprintResponse response = vsBlueprintCatalogueService.queryVsBlueprint(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildVsBlueprintFilter(id, user), null));
				return new ResponseEntity<VsBlueprintInfo>(response.getVsBlueprintInfo().get(0), HttpStatus.OK);
			} else if (site != null) {
				QueryVsBlueprintResponse response = vsBlueprintCatalogueService.queryVsBlueprint(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildSiteFilter(site), null));
				return new ResponseEntity<List<VsBlueprintInfo>>(response.getVsBlueprintInfo(), HttpStatus.OK);
			} else return new ResponseEntity<String>("Not acceptable query parameter", HttpStatus.BAD_REQUEST);
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

	@ApiOperation(value = "Get a Vertical Service Blueprint with a given ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Vertical Service Blueprint with the given ID", response = VsBlueprintInfo.class),
			//@ApiResponse(code = 400, message = "The supplied element contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

	})
	@RequestMapping(value = "/vsblueprint/{vsbId}", method = RequestMethod.GET)
	public ResponseEntity<?> getVsBlueprint(@PathVariable String vsbId, Authentication auth) {
		log.debug("Received request to retrieve VS blueprint with ID " + vsbId);
		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		try {
			String user = authService.getUserFromAuth(auth);
			QueryVsBlueprintResponse response = vsBlueprintCatalogueService.queryVsBlueprint(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildVsBlueprintFilter(vsbId, user), null));
			return new ResponseEntity<VsBlueprintInfo>(response.getVsBlueprintInfo().get(0), HttpStatus.OK);
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

	@ApiOperation(value = "Delete a Vertical Service Blueprint with a given ID")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Empty", response = ResponseEntity.class),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)
	})
	@RequestMapping(value = "/vsblueprint/{vsbId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteVsBlueprint(@PathVariable String vsbId, Authentication auth) {
		log.debug("Received request to delete VS blueprint with ID " + vsbId);
		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = authService.getUserFromAuth(auth);
		if (!user.equals(adminTenant)) {
			log.warn("Request refused as tenant {} is not admin.", user);
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		try {
			vsBlueprintCatalogueService.deleteVsBlueprint(vsbId);
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
