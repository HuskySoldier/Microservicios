package cl.gymtastic.userservice.config;

import cl.gymtastic.userservice.security.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // <--- IMPORTANTE
import org.springframework.security.crypto.password.PasswordEncoder;     // <--- IMPORTANTE
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- ESTE ES EL BEAN QUE FALTABA Y CAUSABA EL ERROR ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // -----------------------------------------------------

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
                    // IMPORTANTE: Permitir que login-service consulte el usuario por email
                    "/users/by-email",
                    "/users/**" 
                ).permitAll()

                // --- RUTAS ADMIN ---
                .requestMatchers(HttpMethod.GET, "/users").hasRole("admin") 
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/users/*/role").hasRole("admin")

                // --- RESTO AUTENTICADO ---
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}