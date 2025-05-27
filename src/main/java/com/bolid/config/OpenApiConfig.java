package com.bolid.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bolid API")
                        .version("1.0")
                        .description("API для сайта Bolid. Документация содержит все доступные эндпоинты для работы с новостями и пользователями.")
                        .contact(new Contact()
                                .name("Bolid Team")
                                .email("mayatin@itmo.ru"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("https://site-bolids-thedenfire.amvera.io")
                                .description("Production server"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server")
                ))
                .tags(List.of(
                        new Tag()
                                .name("Bolid API")
                                .description("API для получения данных о пользователях и новостях")
                ));
    }
} 