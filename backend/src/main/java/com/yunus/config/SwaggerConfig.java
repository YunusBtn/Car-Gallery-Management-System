package com.yunus.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String schemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Car_Sale_System")
                        .description("Araç Alış-Verişini Kontrol Eden Bir Sistem")
                        .version("1.0.0")
                        .contact(new Contact()
                                .email("yunusbtn43@gmail.com")
                                .name("yunus emre bütün")
                                .url("https://github.com/yunusbtn43")))

                // 1) Global olarak tüm endpointlere güvenlik uygula
                .addSecurityItem(new SecurityRequirement().addList(schemeName))

                // 2) Scheme'in ne olduğunu tanımla (JWT Bearer)
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}