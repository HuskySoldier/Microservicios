package cl.gymtastic.attendanceservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI attendanceServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Attendance Service API")
                .description("API para registro de asistencia")
                .version("1.0"));
    }
}