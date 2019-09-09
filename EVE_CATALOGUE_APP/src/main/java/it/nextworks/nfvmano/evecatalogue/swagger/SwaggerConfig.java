package it.nextworks.nfvmano.evecatalogue.swagger;


import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.function.Predicate;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("it.nextworks.nfvmano"))
                .paths(Predicates.or(PathSelectors.ant("/vs/catalogue/**"),
                        PathSelectors.ant("/ctx/catalogue/**"), PathSelectors.ant("/exp/catalogue/**")))
                //.paths(PathSelectors.ant("/vs/catalogue/**"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Blueprint and service Catalogue")
                .description("The API of the 5G App and Service Catalogue")
                .version("1.0")
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
                .contact(new Contact("Nextworks S.r.l.", "http://www.nextworks.it", "info@nextworks.it"))
                .build();
    }







}
