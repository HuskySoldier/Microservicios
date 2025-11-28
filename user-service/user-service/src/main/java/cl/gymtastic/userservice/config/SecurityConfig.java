package cl.gymtastic.userservice.config;

import cl.gymtastic.userservice.security.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // --- RUTAS PÚBLICAS ---
                .requestMatchers(
                    "/login", 
                    "/register", 
                    "/swagger-ui/**", 
                    "/v3/api-docs/**",
                    // ¡ESTA ES LA CLAVE! Permitir que login-service consulte datos:
                    "/users/by-email",
                    // Permitir que checkout-service consulte datos (opcional si no implementas token relay):
                    "/users/**" 
                ).permitAll()

                // --- RUTAS ADMIN ---
                .requestMatchers(HttpMethod.GET, "/users").hasRole("Admin") 
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("Admin")
                .requestMatchers(HttpMethod.PUT, "/users/*/role").hasRole("Admin")

                // --- RESTO AUTENTICADO ---
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}