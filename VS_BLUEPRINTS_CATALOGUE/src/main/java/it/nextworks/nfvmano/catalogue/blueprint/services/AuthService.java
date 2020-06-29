package it.nextworks.nfvmano.catalogue.blueprint.services;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    @Value("${authentication.enable}")
    private boolean authenticationEnable;

    @Value("${keycloak.enabled}")
    private boolean keycloakEnabled;


    @Value("${catalogue.admin}")
    private String adminTenant;

    public  String getUserFromAuth(Authentication auth) {
        if(authenticationEnable){
            if(keycloakEnabled){
                if (auth!=null&& auth.getPrincipal() instanceof KeycloakPrincipal) {
                    KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) auth.getPrincipal();
                    // retrieving username here
                    return kp.getKeycloakSecurityContext().getToken().getPreferredUsername();
                }else return adminTenant;
            }else{
                Object principal = auth.getPrincipal();
                if (!UserDetails.class.isAssignableFrom(principal.getClass())) {
                    throw new IllegalArgumentException("Auth.getPrincipal() does not implement UserDetails");
                }
                return ((UserDetails) principal).getUsername();
            }

        }else return adminTenant;


    }

    public Set<String> getUserRoles(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(authenticationEnable){
            if(keycloakEnabled){
                if (auth.getPrincipal() instanceof KeycloakPrincipal) {
                    KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) auth.getPrincipal();
                    Set<String> roles = kp.getKeycloakSecurityContext().getToken().getRealmAccess().getRoles();
                    log.debug("Retrieved user roles: "+roles);
                    return roles;
                }else return Collections.emptySet();
            }else{
                return Collections.emptySet() ;

            }

        }else return Collections.emptySet();
    }

    public String getUser(){
        return getUserFromAuth(SecurityContextHolder.getContext().getAuthentication());
    }



    public  boolean validateAuthentication(Authentication auth){
        return keycloakEnabled||(!authenticationEnable || auth!=null);

    }


    public boolean isCatalogueAdminUser(Authentication auth){

        return true;
    }
}
