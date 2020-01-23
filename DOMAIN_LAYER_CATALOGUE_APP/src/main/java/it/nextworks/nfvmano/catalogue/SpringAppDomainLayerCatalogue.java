package it.nextworks.nfvmano.catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = {"it.nextworks.nfvmano.catalogues"})
@EntityScan(basePackages = {"it.nextworks.nfvmano"})
@EnableJpaRepositories("it.nextworks.nfvmano")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })

public class SpringAppDomainLayerCatalogue {
    public static void main(String[] args) {
        SpringApplication.run(SpringAppDomainLayerCatalogue.class, args);
    }

}
