package cl.gymtastic.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs
            .authorizeHttpRequests(auth -> auth
                // Permite acceso público a Swagger y a todos los endpoints /products
                .requestMatchers(
                    "/products/**", 
                    "/products/swagger-ui.html", 
                    "/products/swagger-ui/**", 
                    "/products/api-docs", 
                    "/products/api-docs/**"
                ).permitAll() 
                .anyRequest().authenticated()
            );
        
        // Como aún no implementamos tokens, permitimos todo temporalmente
        // (Borra esta línea cuando implementes JWT/OAuth2)
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
