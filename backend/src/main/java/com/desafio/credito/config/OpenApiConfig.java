package com.desafio.credito.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Consulta de Créditos Constituídos")
                        .version("1.0.0")
                        .description("API RESTful desenvolvida em Spring Boot para consulta e detalhamento de créditos constituídos com integração a mensageria Kafka para auditoria.")
                        .contact(new Contact()
                                .name("Josevan Oliveira")
                                .email("josivantarcio@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
