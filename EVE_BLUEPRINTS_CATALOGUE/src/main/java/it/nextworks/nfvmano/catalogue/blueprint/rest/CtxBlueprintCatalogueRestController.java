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
import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxBlueprintInfo;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryCtxBlueprintResponse;
import it.nextworks.nfvmano.catalogue.blueprint.services.CtxBlueprintCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;

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

@Api(tags = "Context Blueprint Catalogue API")
@RestController
@CrossOrigin
@RequestMapping("/portal/catalogue")
public class CtxBlueprintCatalogueRestController {

	private static final Logger log = LoggerFactory.getLogger(CtxBlueprintCatalogueRestController.class);

	@Autowired
	private CtxBlueprintCatalogueService ctxBlueprintCatalogueService;


	@Value("${authentication.enable}")
	private boolean authenticationEnable;

	@Value("${catalogue.admin}")
	private String adminTenant;

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

	public CtxBlueprintCatalogueRestController() { }


	@ApiOperation(value = "Onboard CtxBlueprint")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Creates a new context blueprint and returns its ID.", response = String.class),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 409, message = "There is a conflict with the request", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

	})
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "/ctxblueprint", method = RequestMethod.POST)
	public ResponseEntity<?> createCtxBlueprint(@RequestBody OnboardCtxBlueprintRequest request, Authentication auth) {
		log.debug("Received request to create a CTX blueprint.");
		//TODO: To be improved once the final authentication platform is in place.

		//This was added to allow disabling the authentication system for testing purposes.
		if (!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = getUserFromAuth(auth);

		if (!user.equals(adminTenant)) {
			log.warn("Request refused as tenant {} is not admin.", user);
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		try {
			String ctxBlueprintId = ctxBlueprintCatalogueService.onboardCtxBlueprint(request);
			return new ResponseEntity<String>(ctxBlueprintId, HttpStatus.CREATED);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (AlreadyExistingEntityException e) {
			log.error("CTX Blueprint already existing");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


	}

	@ApiOperation(value = "Get ALL CtxBlueprints")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Return the list of all the CtxBlueprints of the user", response = CtxBlueprintInfo.class, responseContainer = "Set"),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)
	})
	@RequestMapping(value = "/ctxblueprint", method = RequestMethod.GET)
	public ResponseEntity<?> getAllCtxBlueprints(Authentication auth) {
		log.debug("Received request to retrieve all the CTX blueprints.");
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}



		try {
			QueryCtxBlueprintResponse response = ctxBlueprintCatalogueService.queryCtxBlueprint(new GeneralizedQueryRequest(new Filter(), null));
			return new ResponseEntity<List<CtxBlueprintInfo>>(response.getCtxBlueprintInfos(), HttpStatus.OK);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("CTX Blueprints not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


	}

	@ApiOperation(value = "Get CtxBlueprint")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Returns the context blueprint with the given ID", response = CtxBlueprintInfo.class),
			//@ApiResponse(code = 400, message = "The supplied element contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

	})
	@RequestMapping(value = "/ctxblueprint/{ctxbId}", method = RequestMethod.GET)
	public ResponseEntity<?> getCtxBlueprint(@PathVariable String ctxbId, Authentication auth) {
		log.debug("Received request to retrieve CTX blueprint with ID " + ctxbId);
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}


		try {

			QueryCtxBlueprintResponse response = ctxBlueprintCatalogueService.queryCtxBlueprint(new GeneralizedQueryRequest(EveportalCatalogueUtilities.buildCtxBlueprintFilter(ctxbId), null));
			return new ResponseEntity<CtxBlueprintInfo>(response.getCtxBlueprintInfos().get(0), HttpStatus.OK);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("CTX Blueprints not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


	}

	@ApiOperation(value = "Delete CtxBlueprint")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Delete the context blueprint with the given ID", response = ResponseEntity.class),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)
	})
	@RequestMapping(value = "/ctxblueprint/{ctxbId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCtxBlueprint(@PathVariable String ctxbId, Authentication auth) {
		log.debug("Received request to delete CTX blueprint with ID " + ctxbId);
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
			String user = getUserFromAuth(auth);
			if (!user.equals(adminTenant)) {
				log.warn("Request refused as tenant {} is not admin.", user);
				return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
			}
			try {
				ctxBlueprintCatalogueService.deleteCtxBlueprint(ctxbId);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (MalformattedElementException e) {
				log.error("Malformatted request");
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
			} catch (NotExistingEntityException e) {
				log.error("CTX Blueprints not found");
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
			} catch (Exception e) {
				log.error("Internal exception");
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}


	}

}
