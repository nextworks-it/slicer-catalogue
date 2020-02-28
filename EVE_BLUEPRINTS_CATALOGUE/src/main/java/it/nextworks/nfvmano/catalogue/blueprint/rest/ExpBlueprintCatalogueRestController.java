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
import it.nextworks.nfvmano.catalogue.blueprint.BlueprintCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.EveportalCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpBlueprintInfo;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardExpBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryExpBlueprintResponse;
import it.nextworks.nfvmano.catalogue.blueprint.services.AuthService;
import it.nextworks.nfvmano.catalogue.blueprint.services.ExpBlueprintCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "Experiment Blueprint Catalogue API")
@RestController
@CrossOrigin
@RequestMapping("/portal/catalogue")
public class ExpBlueprintCatalogueRestController {

	private static final Logger log = LoggerFactory.getLogger(ExpBlueprintCatalogueRestController.class);

	@Autowired
	private ExpBlueprintCatalogueService expBlueprintCatalogueService;

	@Value("${catalogue.admin}")
	private String adminTenant;

	@Value("${authentication.enable}")
	private boolean authenticationEnable;

	@Value("${keycloak.enabled}")
	private boolean keycloakEnabled;

	@Autowired
	private AuthService authService;

	public ExpBlueprintCatalogueRestController() { }

	@ApiOperation(value = "Onboard ExpBlueprint")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Creates a new experiment blueprint and returns its ID.", response = String.class),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 409, message = "There is a conflict with the request", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

	})
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "/expblueprint", method = RequestMethod.POST)
	public ResponseEntity<?> createExpBlueprint(@RequestBody OnboardExpBlueprintRequest request, Authentication auth) {
		log.debug("Received request to create a EXP blueprint.");
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
			String expBlueprintId = expBlueprintCatalogueService.onboardExpBlueprint(request);
			return new ResponseEntity<String>(expBlueprintId, HttpStatus.CREATED);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request"+e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (AlreadyExistingEntityException e) {
			log.error("EXP Blueprint already existing");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


	@ApiOperation(value = "Get ALL ExpBlueprints")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Retrieve the list of all the experiment blueprints of the user", response = String.class, responseContainer = "Set"),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)
	})

	@RequestMapping(value = "/expblueprint", method = RequestMethod.GET)
	public ResponseEntity<?> getAllExpBlueprints(@RequestParam(required = false) String id, @RequestParam(required = false) String vsbId, Authentication auth) {
		log.debug("Received request to retrieve all the EXP blueprints.");

		//jb: disabled authentication control in order to allow certain hosts to retrieve information without
		//being authenticated
		/*
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}*/

		try {
			if ((id == null) && (vsbId == null)) {
				QueryExpBlueprintResponse response = expBlueprintCatalogueService.queryExpBlueprint(new GeneralizedQueryRequest(new Filter(), null));
				return new ResponseEntity<List<ExpBlueprintInfo>>(response.getExpBlueprintInfo(), HttpStatus.OK);
			} else if (id != null) {
				QueryExpBlueprintResponse response = expBlueprintCatalogueService.queryExpBlueprint(new GeneralizedQueryRequest(EveportalCatalogueUtilities.buildExpBlueprintFilter(id), null));
				return new ResponseEntity<ExpBlueprintInfo>(response.getExpBlueprintInfo().get(0), HttpStatus.OK);
			} else if (vsbId != null) {
				QueryExpBlueprintResponse response = expBlueprintCatalogueService.queryExpBlueprint(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildVsBlueprintFilter(vsbId), null));
				return new ResponseEntity<List<ExpBlueprintInfo>>(response.getExpBlueprintInfo(), HttpStatus.OK);
			} else return new ResponseEntity<String>("Not acceptable query parameter", HttpStatus.BAD_REQUEST);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request"+e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("EXP Blueprints not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


	}

	@ApiOperation(value = "Get ExpBlueprint")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Returns the experiment blueprint with the given ID", response = ExpBlueprintInfo.class),
			//@ApiResponse(code = 400, message = "The supplied element contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

	})
	@RequestMapping(value = "/expblueprint/{expbId}", method = RequestMethod.GET)
	public ResponseEntity<?> getExpBlueprint(@PathVariable String expbId, Authentication auth) {

			log.debug("Received request to retrieve EXP blueprint with ID " + expbId);
		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
			try {
				QueryExpBlueprintResponse response = expBlueprintCatalogueService.queryExpBlueprint(new GeneralizedQueryRequest(EveportalCatalogueUtilities.buildExpBlueprintFilter(expbId), null));
				return new ResponseEntity<ExpBlueprintInfo>(response.getExpBlueprintInfo().get(0), HttpStatus.OK);
			} catch (MalformattedElementException e) {
				log.error("Malformatted request"+e.getMessage());
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
			} catch (NotExistingEntityException e) {
				log.error("EXP Blueprints not found");
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
			} catch (Exception e) {
				log.error("Internal exception");
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}


	}

	@ApiOperation(value = "Delete ExpBlueprint")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Delete the experiment blueprint with the given ID", response = ResponseEntity.class),
			//@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
			//@ApiResponse(code = 404, message = "The element with the supplied id was not found", response = ResponseEntity.class),
			//@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)
	})
	@RequestMapping(value = "/expblueprint/{expbId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteExpBlueprint(@PathVariable String expbId, Authentication auth) {
		if(!authService.validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = authService.getUserFromAuth(auth);
        if (!keycloakEnabled&&!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin and keycloak is not enabled.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }


        try {
            expBlueprintCatalogueService.deleteExpBlueprint(expbId);
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
