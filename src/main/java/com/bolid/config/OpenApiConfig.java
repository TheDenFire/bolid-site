package com.bolid.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bolid API")
                        .version("1.0")
                        .description("API для сайта Bolid")
                        .contact(new Contact()
                                .name("Bolid Team")
                                .email("contact@bolid.com")));
    }
} 