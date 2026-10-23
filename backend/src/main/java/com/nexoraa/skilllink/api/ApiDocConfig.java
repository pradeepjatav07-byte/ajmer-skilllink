package com.nexoraa.skilllink.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocConfig {
    @Bean OpenAPI skillLinkOpenApi() {
        return new OpenAPI().info(new Info().title("Ajmer SkillLink API").version("1.0.0")
                .description("Role-secured REST API for trusted Ajmer local services, FairQuote estimates and bookings."))
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
