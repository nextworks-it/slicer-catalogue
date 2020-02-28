package it.nextworks.nfvmano.evecatalogue;

import it.nextworks.nfvmano.evecatalogue.auth.AuthorizationFilter;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(value = "keycloak.enabled", matchIfMissing = true)
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);


    @Value("${eem.address}")
    private String eemAddress;

    @Value("${ibn.address}")
    private String ibnAddress;


    @Autowired
    public KeycloakClientRequestFactory keycloakClientRequestFactory;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KeycloakRestTemplate keycloakRestTemplate() {
        return new KeycloakRestTemplate(keycloakClientRequestFactory);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs",  "/swagger-resources/**",  "/swagger-ui.html", "/portal/catalogue/translator/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        log.debug("whitelist ip list: ibn: "+ibnAddress+ " eem:"+eemAddress);
        AuthorizationFilter filter = new AuthorizationFilter();
        filter.addIgnoreHost(eemAddress);
        filter.addIgnoreHost(ibnAddress);

        http
                .csrf().disable()
                .cors().and()
                .addFilterBefore(filter, KeycloakPreAuthActionsFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/portal/catalogue/vsblueprint").hasAnyRole("ExperimentDeveloper", "Vertical")
                .antMatchers(HttpMethod.DELETE,"/portal/catalogue/vsblueprint").hasAnyRole("ExperimentDeveloper", "Vertical")
                .antMatchers(HttpMethod.GET,"/portal/catalogue/vsblueprint").hasAnyRole("ExperimentDeveloper", "Vertical", "Experimenter")
                .antMatchers(HttpMethod.POST,"/portal/catalogue/ctxblueprint").hasAnyRole("ExperimentDeveloper")
                .antMatchers(HttpMethod.DELETE,"/portal/catalogue/ctxblueprint").hasAnyRole("ExperimentDeveloper")
                .antMatchers(HttpMethod.GET,"/portal/catalogue/ctxblueprint").hasAnyRole("ExperimentDeveloper", "Experimenter")
                .antMatchers(HttpMethod.POST,"/portal/catalogue/testcaseblueprint").hasAnyRole("ExperimentDeveloper")
                .antMatchers(HttpMethod.DELETE,"/portal/catalogue/testcaseblueprint").hasAnyRole("ExperimentDeveloper")
                .antMatchers(HttpMethod.GET,"/portal/catalogue/testcaseblueprint").hasAnyRole("ExperimentDeveloper", "Experimenter")
                .antMatchers(HttpMethod.POST,"/portal/catalogue/expblueprint").hasAnyRole("ExperimentDeveloper")
                .antMatchers(HttpMethod.DELETE,"/portal/catalogue/expblueprint").hasAnyRole("ExperimentDeveloper")
                .antMatchers(HttpMethod.GET,"/portal/catalogue/expblueprint").hasAnyRole("ExperimentDeveloper", "Experimenter")
                .antMatchers(HttpMethod.POST,"/portal/catalogue/expdescriptor").hasAnyRole("Experimenter")
                .antMatchers(HttpMethod.DELETE,"/portal/catalogue/expdescriptor").hasAnyRole("Experimenter")
                .antMatchers(HttpMethod.GET,"/portal/catalogue/expdescriptor").hasAnyRole("Experimenter")
                .anyRequest()
                .authenticated();
    }


}
