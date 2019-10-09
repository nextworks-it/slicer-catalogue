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
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either ctxress or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package it.nextworks.nfvmano.catalogue.blueprint.rest;

import io.swagger.annotations.Api;
import it.nextworks.nfvmano.catalogue.blueprint.EveportalCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryCtxDescriptorResponse;
import it.nextworks.nfvmano.catalogue.blueprint.services.CtxDescriptorCatalogueService;
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


@Api(tags = "Context Descriptor Catalogue API")
@RestController
@CrossOrigin
@RequestMapping("/portal/catalogue")
public class CtxDescriptorCatalogueRestController {

	private static final Logger log = LoggerFactory.getLogger(CtxDescriptorCatalogueRestController.class);
	
	@Autowired
	private CtxDescriptorCatalogueService ctxDescriptorCatalogueService;




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
	
	public CtxDescriptorCatalogueRestController() { } 
	
	//Note: only GET requests are supported by the REST controller, since create and delete actions are managed through the creation and deletion of the associated experiment descriptor
	
//	@RequestMapping(value = "/ctxdescriptor", method = RequestMethod.POST)
//	public ResponseEntity<?> createCtxDescriptor(@RequestBody OnboardCtxDescriptorRequest request, Authentication auth) {
//		log.debug("Received request to create a CTX descriptor.");
//		String user = getUserFromAuth(auth);
//		if (!request.getTenantId().equals(user)) {
//			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
//		}
//		try {
//			String ctxDescriptorId = ctxDescriptorCatalogueService.onboardCtxDescriptor(request);
//			return new ResponseEntity<String>(ctxDescriptorId, HttpStatus.CREATED);
//		} catch (MalformattedElementException e) {
//			log.error("Malformatted request");
//			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//		} catch (AlreadyExistingEntityException e) {
//			log.error("CTX Blueprint already existing");
//			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
//		} catch (Exception e) {
//			log.error("Internal exception");
//			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
	@RequestMapping(value = "/ctxdescriptor", method = RequestMethod.GET)
	public ResponseEntity<?> getAllCtxDescriptors(Authentication auth) {
		log.debug("Received request to retrieve all the CTX descriptors.");
		if(auth!=null){
			try {
				String user = getUserFromAuth(auth);
				QueryCtxDescriptorResponse response = ctxDescriptorCatalogueService.queryCtxDescriptor(
						new GeneralizedQueryRequest(MgmtCatalogueUtilities.buildTenantFilter(user), null)
				);
				return new ResponseEntity<List<CtxDescriptor>>(response.getCtxDescriptors(), HttpStatus.OK);
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
		}else{
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}

	}
	
	@RequestMapping(value = "/ctxdescriptor/{ctxdId}", method = RequestMethod.GET)
	public ResponseEntity<?> getCtxDescriptor(@PathVariable String ctxdId, Authentication auth) {
		log.debug("Received request to retrieve CTX descriptor with ID " + ctxdId);
		if(!validateAuthentication(auth)){
			log.warn("Unable to retrieve request authentication information");
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}
		String user = getUserFromAuth(auth);


			try {

				QueryCtxDescriptorResponse response = ctxDescriptorCatalogueService.queryCtxDescriptor(
						new GeneralizedQueryRequest(
								EveportalCatalogueUtilities.buildCtxDescriptorFilter(ctxdId, user),
								null
						)
				);
				return new ResponseEntity<CtxDescriptor>(response.getCtxDescriptors().get(0), HttpStatus.OK);
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
	
//	@RequestMapping(value = "/ctxdescriptor/{ctxdId}", method = RequestMethod.DELETE)
//	public ResponseEntity<?> deleteCtxDescriptor(@PathVariable String ctxdId, Authentication auth) {
//		log.debug("Received request to delete CTX descriptor with ID " + ctxdId);
//		try {
//			String user = getUserFromAuth(auth);
//			ctxDescriptorCatalogueService.deleteCtxDescriptor(ctxdId, user);
//			return new ResponseEntity<>(HttpStatus.OK);
//		} catch (MalformattedElementException e) {
//			log.error("Malformatted request");
//			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//		} catch (NotExistingEntityException e) {
//			log.error("CTX Blueprints not found");
//			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
//		} catch (Exception e) {
//			log.error("Internal exception");
//			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

	
}
