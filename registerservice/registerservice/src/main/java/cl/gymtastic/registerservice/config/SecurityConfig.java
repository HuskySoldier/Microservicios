package cl.gymtastic.registerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/register",
                                // Rutas de Swagger: se añaden explícitamente para documentación
                                "/swagger-ui.html", 
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // CORRECCIÓN: Permitimos todas las demás peticiones para este proxy
                        // ya que la lógica de seguridad real está en user-service.
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}