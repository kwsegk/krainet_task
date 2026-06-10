package by.egor.authservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("Документация для auth-service Крайнет")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentification"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentification",
                                new SecurityScheme()
                                        .name("Bearer Authentification")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
