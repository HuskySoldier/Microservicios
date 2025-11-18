package cl.gymtastic.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define el encriptador de contraseñas (BCrypt) para ser inyectado
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura los permisos de acceso
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF
            .authorizeHttpRequests(auth -> auth
                // Permite acceso público a Swagger
                .requestMatchers(
                    "/users/swagger-ui.html",
                    "/users/swagger-ui/**",
                    "/v3/api-docs/**", // <-- Asegúrate de tener esto
                    "/swagger-ui/**",  // <-- Y esto por si acaso
                    "/swagger-ui.html"
                ).permitAll()
                // Permite acceso público a los endpoints de la API
                .requestMatchers(
                    "/register", 
                    "/login", 
                    "/users/**" // Permite /users/{email}, /users/by-email, etc.
                ).permitAll()
                
                // CORRECCIÓN: 
                // Como aún no implementamos tokens JWT, permitimos todo temporalmente
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