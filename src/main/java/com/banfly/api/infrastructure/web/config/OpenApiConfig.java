package com.banfly.api.infrastructure.web.config;


import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Banfly API")
                        .description("REST API for financial client, product and transaction management.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Banfly Team")));
    }

}
