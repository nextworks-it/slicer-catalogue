package it.nextworks.nfvmano.catalogue.blueprint.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.nfvmano.catalogue.blueprint.BlueprintCatalogueUtilities;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnBoardTranslationRuleRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryTranslationRuleResponse;
import it.nextworks.nfvmano.catalogue.blueprint.services.AuthService;
import it.nextworks.nfvmano.catalogue.blueprint.services.TranslationRuleCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Translation Rule Catalogue API")
@RestController
@CrossOrigin
@RequestMapping("/portal/catalogue/translationrule")
public class TranslationRuleCatalogueRestController {

    private static final Logger log = LoggerFactory.getLogger(TranslationRuleCatalogueRestController.class);

    @Autowired
    private TranslationRuleCatalogueService translatorService;

    @Autowired
    private AuthService authService;

    @Value("${catalogue.admin}")
    private String adminTenant;

    public TranslationRuleCatalogueRestController() {	}

    @ApiOperation(value = "Onboard a set of translation rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The list of IDs of the created translation rules.", response = VsdNsdTranslationRule.class, responseContainer = "Set"),
            @ApiResponse(code = 400, message = "The request contains elements impossible to process", response = Error.class),
            @ApiResponse(code = 401, message = "Not allowed to perform the request.", response = Error.class),
            @ApiResponse(code = 409, message = "The list of translation rules already exists.", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createTranslationRules(@RequestBody OnBoardTranslationRuleRequest request, Authentication auth) {

        //SecurityConfig should only allow this REST call to certain roles if keycloack enabled
        log.debug("Received request to onboard translation rules.");

        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        //done only by admin
        String user = authService.getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            List<String> ruleIdList = translatorService.onBoardTranslationRule(request);
            return new ResponseEntity<List<String>>(ruleIdList,HttpStatus.CREATED);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AlreadyExistingEntityException e) {
            log.error("Translation rules already existing");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get all translation rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all the translation rules", response = VsdNsdTranslationRule.class, responseContainer = "Set"),
            @ApiResponse(code = 400, message = "The request contains elements impossible to process", response = Error.class),
            @ApiResponse(code = 401, message = "Not allowed to perform the request.", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllTranslationRules(Authentication auth) {
        log.debug("Received request to retrieve translation rules.");
        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            String user = authService.getUserFromAuth(auth);
            if(!user.equals(adminTenant)){
                log.warn("Only admin can retrieve all translation rules.");
                return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }
            QueryTranslationRuleResponse response = translatorService.queryTranslationRule(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildTenantFilter(user), null));
            return new ResponseEntity<List<VsdNsdTranslationRule>>(response.getVsdNsdTranslationRules(), HttpStatus.OK);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get all translation rules of specific Blueprint.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all the translation rules of a specific Blueprint", response = VsdNsdTranslationRule.class, responseContainer = "Set"),
            @ApiResponse(code = 400, message = "The request contains elements impossible to process", response = Error.class),
            @ApiResponse(code = 401, message = "Not allowed to perform the request.", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    @RequestMapping(value = "/{blueprintId}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllTranslationRulesWithId(@PathVariable String blueprintId, Authentication auth) {
        if(blueprintId == null){
            log.error("BLueprint id not present.");
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
        log.debug("Received request to retrieve translation rules of Blueprint " + blueprintId + ".");
        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            String user = authService.getUserFromAuth(auth);
            log.debug("Retrieving translation rules of Blueprint " + blueprintId);
            QueryTranslationRuleResponse response = translatorService.queryTranslationRule(new GeneralizedQueryRequest(BlueprintCatalogueUtilities.buildVsBlueprintFilter(blueprintId, user), null));
            return new ResponseEntity<List<VsdNsdTranslationRule>>(response.getVsdNsdTranslationRules(), HttpStatus.OK);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @ApiOperation(value = "Update a set of Translation Rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Rules updated."),
            @ApiResponse(code = 400, message = "The request contains elements impossible to process", response = Error.class),
            @ApiResponse(code = 401, message = "Not allowed to perform the request.", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTranslationRule(@RequestBody OnBoardTranslationRuleRequest request, Authentication auth) {
        log.debug("Received request to update translation rules");
        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        //done only by admin
        String user = authService.getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            translatorService.updateTranslationRule(request);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @ApiOperation(value = "Update a translation rule with specific id.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The id of the updated rule.", response = String.class),
            @ApiResponse(code = 400, message = "The request contains elements impossible to process", response = Error.class),
            @ApiResponse(code = 401, message = "Not allowed to perform the request.", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)

    })
    @RequestMapping(value = "/{ruleId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTranslationRule(@PathVariable String ruleId, @RequestBody VsdNsdTranslationRule rule, Authentication auth) {
        log.debug("Received request to update translation rule with id " + ruleId);
        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        //done only by admin
        String user = authService.getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if(ruleId == null || rule == null){
            return new ResponseEntity<String>("Missing rule id or translation rule body.", HttpStatus.BAD_REQUEST);
        }

        try {
            String id = translatorService.updateTranslationRuleWithId(rule,ruleId);
            return new ResponseEntity<String>(id,HttpStatus.OK);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Delete a translation rule with id ruleId.
     * @param ruleId , id of translation rule
     * @param auth , authentication
     */
    @ApiOperation(value = "Delete a translation rule with a specific id.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Rule deleted."),
            @ApiResponse(code = 401, message = "Not allowed to perform the request.", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    @RequestMapping(value = "/{ruleId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTranslationRule(@PathVariable String ruleId, Authentication auth) {
        log.debug("Received request to delete a translation rule with id " + ruleId);
        if(!authService.validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        //done only by admin
        String user = authService.getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            translatorService.deleteTranslationRule(ruleId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (FailedOperationException e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

