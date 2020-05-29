package it.nextworks.nfvmano.catalogue.blueprint.services;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


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



    public  boolean validateAuthentication(Authentication auth){
        return keycloakEnabled||(!authenticationEnable || auth!=null);

    }


    public boolean isCatalogueAdminUser(Authentication auth){

        return true;
    }
}
