package it.nextworks.nfvmano.catalogues.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@EntityScan(basePackages = "it.nextworks.nfvmano")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })

public class SpringAppNSTemplateCatalogue {

	public static void main(String[] args) {
		SpringApplication.run(SpringAppNSTemplateCatalogue.class, args);
	}

}