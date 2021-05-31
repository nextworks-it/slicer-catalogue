package it.nextworks.nfvmano.catalogue.arbitrator.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import it.nextworks.nfvmano.catalogue.arbitrator.im.ArbitratorPolicyInfo;
import it.nextworks.nfvmano.catalogue.arbitrator.messages.OnboardArbitratorPolicyRequest;
import it.nextworks.nfvmano.catalogue.arbitrator.messages.QueryArbitrationPolicyResponse;
import it.nextworks.nfvmano.catalogue.arbitrator.services.ArbitratorPolicyService;



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

@Api(tags = "Arbitrator Policy Catalogue API")
@RestController
@CrossOrigin
@RequestMapping("/portal/catalogue/arbitrator")
public class ArbitratorPolicyCatalogueRestController {

    private static final Logger log = LoggerFactory.getLogger(ArbitratorPolicyCatalogueRestController.class);

    @Autowired
    private ArbitratorPolicyService arbitratorPolicyService;
    @Value("${authentication.enable}")
    private boolean authenticationEnable;


    @Value("${catalogue.admin}")

    private String adminTenant;




    public  String getUserFromAuth(Authentication auth) {
        if(authenticationEnable){

            Object principal = auth.getPrincipal();
            if (!UserDetails.class.isAssignableFrom(principal.getClass())) {
                throw new IllegalArgumentException("Auth.getPrincipal() does not implement UserDetails");
            }
            return ((UserDetails) principal).getUsername();


        }else return adminTenant;


    }

    public  boolean validateAuthentication(Authentication auth){
        return (!authenticationEnable || auth!=null);

    }

    public ArbitratorPolicyCatalogueRestController() {	}

    @ApiOperation(value = "Onboard a set of Arbitration Policies.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The list of IDs of the created arbitration policies.", response = String.class, responseContainer = "Set"),
            @ApiResponse(code = 400, message = "The request contains elements impossible to process", response = Error.class),
            @ApiResponse(code = 401, message = "Not allowed to perform the request.", response = Error.class),
            @ApiResponse(code = 409, message = "The list of arbitration polices already exists.", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createArbitrationPolicies(@RequestBody OnboardArbitratorPolicyRequest request, Authentication auth) {

        //SecurityConfig should only allow this REST call to certain roles if keycloack enabled
        log.debug("Received request to onboard arbitration policies.");

        if(!validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        //done only by admin
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            List<String> ruleIdList = arbitratorPolicyService.onboardArbitrationPolicy(request);
            return new ResponseEntity<List<String>>(ruleIdList,HttpStatus.CREATED);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AlreadyExistingEntityException e) {
            log.error("Arbitration policies rules already existing");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get all arbitration policies")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all the arbitration policies", response = ArbitratorPolicyInfo.class, responseContainer = "Set"),
            @ApiResponse(code = 400, message = "The request contains elements impossible to process", response = Error.class),
            @ApiResponse(code = 401, message = "Not allowed to perform the request.", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllArbitrationPolicies(Authentication auth) {
        log.debug("Received request to retrieve arbitration policies");
        if(!validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            String user = getUserFromAuth(auth);
            if(!user.equals(adminTenant)){
                log.warn("Only admin can retrieve all arbitration policies");
                return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }
            QueryArbitrationPolicyResponse response = arbitratorPolicyService.queryArbitrationPolicy(new GeneralizedQueryRequest(null, null));
            return new ResponseEntity<List<ArbitratorPolicyInfo>>(response.getPolicies(), HttpStatus.OK);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    
    @ApiOperation(value = "Update a set of Arbitration policies.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Policies updated."),
            @ApiResponse(code = 400, message = "The request contains elements impossible to process", response = Error.class),
            @ApiResponse(code = 401, message = "Not allowed to perform the request.", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<?> updateArbitrationPolicy(@RequestBody OnboardArbitratorPolicyRequest request, Authentication auth) {
        log.debug("Received request to update arbitration policies");
        if(!validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        //done only by admin
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            arbitratorPolicyService.updateArbitrationPolicy(request);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    /**
     * Delete a arbitration policy with id ruleId.
     * @param policyId , id of arbitration policy
     * @param auth , authentication
     */
    @ApiOperation(value = "Delete a arbitration policy with a specific id.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Rule deleted."),
            @ApiResponse(code = 401, message = "Not allowed to perform the request.", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    @RequestMapping(value = "/{policyId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteArbitrationPolicye(@PathVariable String policyId, Authentication auth) {
        log.debug("Received request to delete a arbitration policy with id " + policyId);
        if(!validateAuthentication(auth)){
            log.warn("Unable to retrieve request authentication information");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        //done only by admin
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            arbitratorPolicyService.deleteArbitrationPolicy(policyId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (FailedOperationException e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

