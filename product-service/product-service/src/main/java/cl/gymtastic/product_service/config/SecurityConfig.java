package cl.gymtastic.product_service.config;

import cl.gymtastic.product_service.security.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer; // <--- Importante
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // <--- Importante
import org.springframework.web.cors.CorsConfigurationSource; // <--- Importante
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // <--- Importante

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // <--- 1. ACTIVAR CORS AQUÍ
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // Documentación
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // LEER productos (Público)
                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                
                // Endpoints internos
                .requestMatchers(HttpMethod.POST, "/products/decrement-stock").permitAll()

                // Acciones de ADMIN (Usamos "admin" en minúscula como acordamos)
                .requestMatchers(HttpMethod.POST, "/products").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("admin")
                
                .anyRequest().authenticated() 
            );
            
        return http.build();
    }

    // --- 2. CONFIGURACIÓN DETALLADA DE CORS ---
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir explícitamente tu Frontend
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        
        // Permitir todos los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Permitir headers (como Authorization)
        configuration.setAllowedHeaders(List.of("*"));
        
        // Permitir credenciales (cookies/tokens)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}