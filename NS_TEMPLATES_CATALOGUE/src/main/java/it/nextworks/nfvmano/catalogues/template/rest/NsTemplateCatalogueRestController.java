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

import it.nextworks.nfvmano.catalogue.template.elements.NsTemplateInfo;
import it.nextworks.nfvmano.catalogue.template.messages.OnBoardNsTemplateRequest;
import it.nextworks.nfvmano.catalogue.template.messages.QueryNsTemplateResponse;
import it.nextworks.nfvmano.catalogues.template.TemplateCatalogueUtilities;
import it.nextworks.nfvmano.catalogues.template.services.NsTemplateCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.templates.GeographicalAreaInfo;
import it.nextworks.nfvmano.libs.ifa.templates.plugAndPlay.Actuation;
import it.nextworks.nfvmano.libs.ifa.templates.plugAndPlay.PpFunction;
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
@RequestMapping("/ns/catalogue")
public class NsTemplateCatalogueRestController {

    private static final Logger log = LoggerFactory.getLogger(NsTemplateCatalogueRestController.class);

    @Autowired
    private NsTemplateCatalogueService nsTemplateCatalogueService;

    @Value("${catalogue.admin}")  //FIXME Check about the NSMF Admin tenant
    private String adminTenant;

    public NsTemplateCatalogueRestController() { }

    private static String getUserFromAuth(Authentication auth) {
        if(auth==null)
            return "";
        Object principal = auth.getPrincipal();
        if (!UserDetails.class.isAssignableFrom(principal.getClass())) {
            throw new IllegalArgumentException("Auth.getPrincipal() does not implement UserDetails");
        }
        return ((UserDetails) principal).getUsername();
    }

    @RequestMapping(value = "/nstemplate", method = RequestMethod.POST)
    public ResponseEntity<?> createNsTemplate(@RequestBody OnBoardNsTemplateRequest request, Authentication auth) {
        log.debug("Received request to create a NS Template.");
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            String nsTemplateId = nsTemplateCatalogueService.onBoardNsTemplate(request);
            return new ResponseEntity<String>(nsTemplateId, HttpStatus.CREATED);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AlreadyExistingEntityException e) {
            log.error("NS Template already existing");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @RequestMapping(value = "/nstemplate", method = RequestMethod.GET)
    public ResponseEntity<?> getAllNsTemplates(Authentication auth) {
        log.debug("Received request to retrieve all the NS Templates.");
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            QueryNsTemplateResponse response = nsTemplateCatalogueService.queryNsTemplate(new GeneralizedQueryRequest(new Filter(), null));
            return new ResponseEntity<List<NsTemplateInfo>>(response.getNsTemplateInfos(), HttpStatus.OK);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotExistingEntityException e) {
            log.error("NS Templates not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/nstemplate/{nstId}", method = RequestMethod.GET)
    public ResponseEntity<?> getNsTemplate(@PathVariable String nstId,Authentication auth) {
        log.debug("Received request to retrieve Ns Template with ID " + nstId);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            QueryNsTemplateResponse response = nsTemplateCatalogueService.queryNsTemplate(new GeneralizedQueryRequest(TemplateCatalogueUtilities.buildNsTemplateFilter(nstId), null));
            return new ResponseEntity<NsTemplateInfo>(response.getNsTemplateInfos().get(0), HttpStatus.OK);

        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotExistingEntityException e) {
            log.error("NS Template not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/nstemplate/kpi/{nstUuid}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateKpiNsTemplate(@RequestBody List<String> kpi, @PathVariable String nstUuid, Authentication auth) {
        log.debug("Received request to update KPI of Ns Template with UUID " + nstUuid);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            nsTemplateCatalogueService.updateKpiNsTemplate(nstUuid, kpi);
            log.info("KPIs of Network Slice Template with UUID "+nstUuid+" correctly updated");
            return new ResponseEntity<String>("KPIs correctly updated", HttpStatus.OK);

        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotExistingEntityException e) {
            log.error("NS Template not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/nstemplate/geoLocation/{nstUuid}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateGeoNsTemplate(@RequestBody List<GeographicalAreaInfo> geographicalAreaInfoList, @PathVariable String nstUuid, Authentication auth) {
        log.debug("Received request to update geographical location of Ns Template with UUID " + nstUuid);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            nsTemplateCatalogueService.updateGeoLocationNsTemplate(nstUuid, geographicalAreaInfoList);
            log.info("Geographical location of Network Slice Template with UUID "+nstUuid+" correctly updated");
            return new ResponseEntity<String>("Geographical location correctly updated", HttpStatus.OK);

        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotExistingEntityException e) {
            log.error("NS Template not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/nstemplate/actuation/{nstUuid}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateActuationNsTemplate(@RequestBody List<Actuation> actuationList, @PathVariable String nstUuid, Authentication auth) {
        log.debug("Received request to update KPI of Ns Template with UUID " + nstUuid);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            nsTemplateCatalogueService.updateActuationNsTemplate(nstUuid, actuationList);
            log.info("KPIs of Network Slice Template with UUID "+nstUuid+" correctly updated");
            return new ResponseEntity<String>("Actuation correctly updated", HttpStatus.OK);

        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotExistingEntityException e) {
            log.error("NS Template not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/nstemplate/pp/{nstUuid}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateppFunctionsNsTemplate(@RequestBody List<PpFunction> ppFunctions, @PathVariable String nstUuid, Authentication auth) {
        log.debug("Received request to update PP of Ns Template with UUID " + nstUuid);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            nsTemplateCatalogueService.updatePPfunctionsNsTemplate(nstUuid, ppFunctions);
            log.info("PP functions of Network Slice Template with UUID "+nstUuid+" correctly updated");
            return new ResponseEntity<String>("PP functions correctly updated", HttpStatus.OK);

        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotExistingEntityException e) {
            log.error("NS Template not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/nstemplate/{nstId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteNsTemplate(@PathVariable String nstId, Authentication auth) {
        log.debug("Received request to delete NS Template with ID " + nstId);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            nsTemplateCatalogueService.deleteNsTemplate(nstId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotExistingEntityException e) {
            log.error("NS Template not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
