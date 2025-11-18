package cl.gymtastic.attendanceservice.config;

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
                    "/attendance/**", 
                    "/attendance/swagger-ui.html", 
                    "/attendance/swagger-ui/**", 
                    "/attendance/api-docs", 
                    "/attendance/api-docs/**"
                  
                ).permitAll() 
                
                // CORRECCIÓN: 
                // Como aún no implementamos tokens, permitimos todo temporalmente
                // (Borra esta línea cuando implementes JWT/OAuth2 y descomenta la de abajo)
                .anyRequest().permitAll()
                
                // (La regla original era: .anyRequest().authenticated())
            );
        
        // BORRADO: 
        // Se eliminó la segunda llamada a http.authorizeHttpRequests(...) 
        // que causaba el conflicto.

        return http.build();
    }
}