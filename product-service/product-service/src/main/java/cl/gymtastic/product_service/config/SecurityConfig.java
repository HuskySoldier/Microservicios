package cl.gymtastic.product_service.config;

import cl.gymtastic.product_service.security.JwtTokenFilter;
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
                // Documentación pública
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // LEER productos: Público (para que la tienda se vea sin login)
                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                
                // MODIFICAR productos (Crear, Editar, Borrar): SOLO ADMIN
                .requestMatchers(HttpMethod.POST, "/products").hasRole("Admin")
                .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("Admin")
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("Admin")
                
                // DESCONTAR STOCK (Usado por Checkout): Requiere autenticación (Cliente o Admin)
                .requestMatchers(HttpMethod.POST, "/products/decrement-stock").authenticated()

                .anyRequest().authenticated() 
            );
            
        return http.build();
    }
}