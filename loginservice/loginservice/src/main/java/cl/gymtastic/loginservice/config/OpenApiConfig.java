package cl.gymtastic.loginservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI loginserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Login Service API")
                .description("API para iniciar sesión (Proxy a User Service)")
                .version("1.0"));
    }
}