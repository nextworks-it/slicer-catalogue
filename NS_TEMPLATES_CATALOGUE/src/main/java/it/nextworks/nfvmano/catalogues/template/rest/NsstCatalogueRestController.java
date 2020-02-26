package it.nextworks.nfvmano.catalogues.template.rest;

import it.nextworks.nfvmano.catalogue.template.messages.OnBoardNsstRequest;
import it.nextworks.nfvmano.catalogues.template.services.NsstCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.templates.nsst.NSST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/ns/catalogue")
public class NsstCatalogueRestController {
    private static final Logger log = LoggerFactory.getLogger(NsTemplateCatalogueRestController.class);

    @Autowired
    private NsstCatalogueService nsstCatalogueService;


    @Value("${catalogue.admin}")  //FIXME Check about the NSMF Admin tenant
    private String adminTenant;

    public NsstCatalogueRestController() { }

    private static String getUserFromAuth(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (!UserDetails.class.isAssignableFrom(principal.getClass())) {
            throw new IllegalArgumentException("Auth.getPrincipal() does not implement UserDetails");
        }
        return ((UserDetails) principal).getUsername();
    }

    @RequestMapping(value = "/nsst", method = RequestMethod.POST)
    public ResponseEntity<?> createNsst(@RequestBody OnBoardNsstRequest request, Authentication auth) {
        log.debug("Received request to create a NSST.");
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            String nsstId = nsstCatalogueService.onBoardNsst(request);
            return new ResponseEntity<String>(nsstId, HttpStatus.CREATED);
        } catch (MalformattedElementException e) {
            log.error("Malformatted request");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AlreadyExistingEntityException e) {
            log.error("NSST already existing");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @RequestMapping(value = "/nsst/{nsstId}", method = RequestMethod.GET)
    public ResponseEntity<?> getNsTemplate(@PathVariable String nsstId) {
        log.debug("Received request to retrieve nnst with ID " + nsstId);
        try {
            NSST nsst = nsstCatalogueService.getNsstById(nsstId);
            return new ResponseEntity<NSST>(nsst, HttpStatus.OK);
        } catch (NotExistingEntityException e) {
            log.error("NS Template not found");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Internal exception");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/nsst/{nsstId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteNsTemplate(@PathVariable String nsstId, Authentication auth) {
        log.debug("Received request to delete NSST with ID " + nsstId);
        String user = getUserFromAuth(auth);
        if (!user.equals(adminTenant)) {
            log.warn("Request refused as tenant {} is not admin.", user);
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            nsstCatalogueService.deleteNsst(nsstId);
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
