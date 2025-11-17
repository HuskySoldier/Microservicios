package cl.gymtastic.loginservice.service;

import cl.gymtastic.loginservice.client.UserClient;
import cl.gymtastic.loginservice.dto.LoginRequest;
import cl.gymtastic.loginservice.dto.LoginResponse;
import cl.gymtastic.loginservice.dto.UserProfileResponse;
import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class LoginService {

    @Autowired
    private UserClient userClient; // Cliente para llamar a user-service

    @Autowired
    private PasswordEncoder passwordEncoder; // Para comparar contraseñas

    public LoginResponse login(@Valid LoginRequest request) {
        
        UserProfileResponse user;
        String email = request.getEmail().trim().toLowerCase();

        // 1. Buscar al usuario en user-service
        try {
            ResponseEntity<UserProfileResponse> response = userClient.buscarPorEmail(email);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                // Esto no debería pasar si 404 lanza FeignException.NotFound
                return new LoginResponse(false, null, "Credenciales inválidas (Error al buscar)", null);
            }
            user = response.getBody();

        } catch (FeignException.NotFound e) {
            // Usuario no encontrado (404)
            return new LoginResponse(false, null, "Credenciales inválidas (Usuario no existe)", null);
        } catch (Exception e) {
            // Otro error (ej. user-service caído)
            return new LoginResponse(false, null, "Error de comunicación: " + e.getMessage(), null);
        }

        // 2. Verificar la contraseña
        // Compara la contraseña en texto plano (request) con el hash (de la BD)
        if (passwordEncoder.matches(request.getPassword().trim(), user.getPassHash())) {
            // ¡Éxito!
            
            // Limpiamos el hash antes de devolverlo a la app
            user.setPassHash(null); 
            
            return new LoginResponse(
                true,
                "fake-jwt-token-" + user.getEmail(), // Token simulado
                "Login exitoso",
                user // Devolvemos el perfil completo
            );
        } else {
            // Contraseña incorrecta
            return new LoginResponse(false, null, "Credenciales inválidas (Contraseña incorrecta)", null);
        }
    }
}