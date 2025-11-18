package cl.gymtastic.loginservice.config;

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

    /**
     * Define el Bean de PasswordEncoder para que 
     * LoginService pueda inyectarlo y comparar contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF
            .authorizeHttpRequests(auth -> auth
                // Permite acceso público a Swagger
                .requestMatchers(
                    "/login/swagger-ui.html", 
                    "/login/swagger-ui/**", 
                    "/login/api-docs", 
                    "/login/api-docs/**"
                ).permitAll()
                // Permite acceso público al endpoint de login
                .requestMatchers("/login").permitAll()
                
                // CORRECCIÓN: 
                // Permitimos todo temporalmente para pruebas
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