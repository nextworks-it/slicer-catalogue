/*
 * Copyright (c) 2019 Nextworks s.r.l
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.nextworks.nfvmano.catalogues.template.rest;

import it.nextworks.nfvmano.catalogue.template.elements.DomainLayer;
import it.nextworks.nfvmano.catalogues.template.services.DomainLayerCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
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

@RestController
@CrossOrigin
@RequestMapping("/domainLayer/catalogue")
public class DomainLayerCatalogueRestController {

    private static final Logger log = LoggerFactory.getLogger(DomainLayerCatalogueRestController.class);

    @Autowired
    private DomainLayerCatalogueService domainLayerCatalogueService;

    @Value("${catalogue.admin}")
    private String adminTenant;

    public DomainLayerCatalogueRestController() { }

    private static String getUserFromAuth(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (!UserDetails.class.isAssignableFrom(principal.getClass())) {
            throw new IllegalArgumentException("Auth.getPrincipal() does not implement UserDetails");
        }
        return ((UserDetails) principal).getUsername();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createDomainLayer(@RequestBody DomainLayer domainLayer, Authentication auth) {
        log.debug("Received request to create a Domain layer.");
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            Long domainLayerId = domainLayerCatalogueService.onBoardDomainLayer(domainLayer);
            return new ResponseEntity<String>(String.valueOf(domainLayerId), HttpStatus.CREATED);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AlreadyExistingEntityException e) {
            log.error("Domain layer already existing");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllDomainLayer() {
        log.debug("Received request to retrieve all domain layers.");
        return new ResponseEntity<List<DomainLayer>>(domainLayerCatalogueService.getAllDomainLayers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{domainLayerId}", method = RequestMethod.GET)
    public ResponseEntity<?> getDomainLayer(@PathVariable Long domainLayerId, Authentication auth) {
        log.debug("Received request to retrieve Domain layer with ID " + domainLayerId);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
             return new ResponseEntity<DomainLayer>(domainLayerCatalogueService.getDomainLayer(domainLayerId), HttpStatus.OK);
        }  catch (NotExistingEntityException e) {
            log.error("Domain layer not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{domainLayerId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDomainLayer(@PathVariable Long domainLayerId, Authentication auth) {
        log.debug("Received request to delete Domain layer with ID " + domainLayerId);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            domainLayerCatalogueService.deleteDomainLayer(domainLayerId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotExistingEntityException e) {
            log.error("NS Template not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
