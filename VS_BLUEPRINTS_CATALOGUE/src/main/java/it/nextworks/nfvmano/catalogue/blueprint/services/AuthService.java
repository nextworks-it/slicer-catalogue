package it.nextworks.nfvmano.catalogue.blueprint.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


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
}
