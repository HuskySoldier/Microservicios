package cl.gymtastic.registerservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI registerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Register Service API")
                .description("API para crear nuevas cuentas (Proxy a User Service)")
                .version("1.0"));
    }
}