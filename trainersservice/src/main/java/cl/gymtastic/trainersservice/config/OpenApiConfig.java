package cl.gymtastic.trainersservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI trainersServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Trainers Service API")
                .description("API para gestión de entrenadores")
                .version("1.0"));
    }
}