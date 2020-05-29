package it.nextworks.nfvmano.catalogue.blueprint.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.nfvmano.catalogue.blueprint.services.AuthService;
import it.nextworks.nfvmano.catalogue.blueprint.services.CatalogueAdminService;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Api(tags = "EVE Portal Catalogue admin API")
@RestController
@CrossOrigin
@RequestMapping("/portal/admin/")
public class CatalogueAdminRestController {

    private static final Logger log = LoggerFactory.getLogger(CatalogueAdminRestController.class);
    @Value("${keycloak.enabled}")
    private boolean keycloakEnabled;

    @Value("${catalogue.admin}")
    private String adminTenant;


    @Autowired
    private AuthService authService;

    @Autowired
    private CatalogueAdminService catalogueAdminService;

    @ApiOperation(value = "Delete CtxBlueprint")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deletes a CtxBlueprint", response = String.class),
            //@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
            //@ApiResponse(code = 409, message = "There is a conflict with the request", response = ResponseEntity.class),
            //@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

    })
    @RequestMapping(value = "/catalogue/deleteCtxBlueprint/{ctxbId}", method = RequestMethod.DELETE)

    public ResponseEntity<?> deleteCtxBlueprint(@PathVariable String ctxbId, Authentication auth) {
        log.debug("Received request to delete CTX blueprint with ID " + ctxbId);
        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String user = authService.getUserFromAuth(auth);

        //TODO: add endpoint control to Keycloak configuration. This should not be needed
        boolean catalogueAdmin = authService.isCatalogueAdminUser(auth);
        if (!keycloakEnabled&&!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin and keycloak is not enabled", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            catalogueAdminService.forceDeleteCtxBlueprint(ctxbId, user);
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

    @ApiOperation(value = "Delete CtxBlueprint")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deletes a CtxBlueprint", response = String.class),
            //@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
            //@ApiResponse(code = 409, message = "There is a conflict with the request", response = ResponseEntity.class),
            //@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

    })
    @RequestMapping(value = "/catalogue/deleteTcBlueprint/{tcbId}", method = RequestMethod.DELETE)

    public ResponseEntity<?> deleteTcBlueprint(@PathVariable String tcbId, Authentication auth) {
        log.debug("Received request to delete CTX blueprint with ID " + tcbId);
        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String user = authService.getUserFromAuth(auth);

        //TODO: add endpoint control to Keycloak configuration. This should not be needed
        boolean catalogueAdmin = authService.isCatalogueAdminUser(auth);
        if (!keycloakEnabled&&!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin and keycloak is not enabled", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            catalogueAdminService.forceDeleteTcBlueprint(tcbId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotExistingEntityException e) {
            log.error("TC Blueprint not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }



    @ApiOperation(value = "Delete VsBlueprint")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deletes a VsBlueprint.", response = String.class),
            //@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
            //@ApiResponse(code = 409, message = "There is a conflict with the request", response = ResponseEntity.class),
            //@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

    })
    @RequestMapping(value = "/catalogue/deleteVsBlueprint/{vsbId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteVsBlueprint(@PathVariable String vsbId, Authentication auth) {
        log.debug("Received request to delete VS blueprint with ID " + vsbId);
        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String user = authService.getUserFromAuth(auth);

        //TODO: add endpoint control to Keycloak configuration. This should not be needed
        boolean catalogueAdmin = authService.isCatalogueAdminUser(auth);
        if (!keycloakEnabled&&!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin and keycloak is not enabled", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            catalogueAdminService.forceDeleteVsBlueprint(vsbId, user);
            return new ResponseEntity<>(HttpStatus.OK);
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

    @ApiOperation(value = "Delete ExpBlueprint")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deletes a ExpBlueprint.", response = String.class),
            //@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
            //@ApiResponse(code = 409, message = "There is a conflict with the request", response = ResponseEntity.class),
            //@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

    })
    @RequestMapping(value = "/catalogue/deleteExpBlueprint/{expbId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteExpBlueprint(@PathVariable String expbId, Authentication auth) {
        log.debug("Received request to delete EXP blueprint with ID " + expbId);
        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String user = authService.getUserFromAuth(auth);

        //TODO: add endpoint control to Keycloak configuration. This should not be needed
        boolean catalogueAdmin = authService.isCatalogueAdminUser(auth);
        if (!keycloakEnabled&&!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin and keycloak is not enabled", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            catalogueAdminService.forceDeleteExpBlueprint(expbId, user);
            return new ResponseEntity<>(HttpStatus.OK);
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

    @ApiOperation(value = "Delete ExpDescriptor")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deletes a ExpDescriptor.", response = String.class),
            //@ApiResponse(code = 400, message = "The request contains elements impossible to process", response = ResponseEntity.class),
            //@ApiResponse(code = 409, message = "There is a conflict with the request", response = ResponseEntity.class),
            //@ApiResponse(code = 500, message = "Status 500", response = ResponseEntity.class)

    })
    @RequestMapping(value = "/catalogue/deleteExpDescriptor/{expdId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteExpDescriptor(@PathVariable String expdId, Authentication auth) {

        log.debug("Received request to delete EXP blueprint with ID " + expdId);
        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String user = authService.getUserFromAuth(auth);

        //TODO: add endpoint control to Keycloak configuration. This should not be needed
        boolean catalogueAdmin = authService.isCatalogueAdminUser(auth);
        if (!keycloakEnabled&&!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin and keycloak is not enabled", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            catalogueAdminService.forceDeleteExpDescriptor(expdId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotExistingEntityException e) {
            log.error("Expd not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }




}
