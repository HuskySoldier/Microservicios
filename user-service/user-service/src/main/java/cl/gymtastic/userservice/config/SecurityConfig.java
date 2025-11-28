package cl.gymtastic.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import cl.gymtastic.userservice.security.JwtTokenFilter;
import org.springframework.http.HttpMethod;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // Registramos el filtro ANTES del filtro estándar de autenticación
            .addFilterBefore(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/login", "/register", "/swagger-ui/**").permitAll()
                // Rutas protegidas
                .requestMatchers(HttpMethod.GET, "/users").hasRole("Admin") // Solo Admin ve todos
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("Admin") // Solo Admin borra
                .anyRequest().authenticated() // Todo lo demás requiere login
            );
        
        return http.build();
    }
}