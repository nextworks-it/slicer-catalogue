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

package it.nextworks.nfvmano.catalogues.domainLayer.rest;

import it.nextworks.nfvmano.catalogue.domainLayer.Domain;
import it.nextworks.nfvmano.catalogues.domainLayer.services.DomainCatalogueService;
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
public class DomainCatalogueRestController {

    private static final Logger log = LoggerFactory.getLogger(DomainCatalogueRestController.class);

    @Autowired
    private DomainCatalogueService domainCatalogueService;

    @Value("${catalogue.admin}")
    private String adminTenant;

    public DomainCatalogueRestController() { }

    private static String getUserFromAuth(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (!UserDetails.class.isAssignableFrom(principal.getClass())) {
            throw new IllegalArgumentException("Auth.getPrincipal() does not implement UserDetails");
        }
        return ((UserDetails) principal).getUsername();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createDomain(@RequestBody Domain domain, Authentication auth) {
        log.debug("Received request to create a domain.");
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            Long domainId = domainCatalogueService.onBoardDomain(domain);
            return new ResponseEntity<String>(String.valueOf(domainId), HttpStatus.CREATED);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AlreadyExistingEntityException e) {
            log.error("Domain already existing");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllDomain() {
        log.debug("Received request to retrieve all domains.");
        return new ResponseEntity<List<Domain>>(domainCatalogueService.getAllDomains(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{domainId}", method = RequestMethod.GET)
    public ResponseEntity<?> getDomain(@PathVariable String domainId, Authentication auth) {
        log.debug("Received request to retrieve Domain with ID " + domainId);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
             return new ResponseEntity<Domain>(domainCatalogueService.getDomain(domainId), HttpStatus.OK);
        }  catch (NotExistingEntityException e) {
            log.error("Domain not found with ID "+domainId+" not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{domainId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDomain(@PathVariable String domainId, Authentication auth) {
        log.debug("Received request to delete Domain with ID " + domainId);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            domainCatalogueService.deleteDomain(domainId);
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
