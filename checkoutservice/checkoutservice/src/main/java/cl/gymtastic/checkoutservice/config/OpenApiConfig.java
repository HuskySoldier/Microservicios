package cl.gymtastic.checkoutservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI checkoutServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Checkout Service API")
                .description("API orquestadora de pagos")
                .version("1.0"));
    }
}