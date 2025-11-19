package cl.gymtastic.loginservice.service;

import cl.gymtastic.loginservice.client.UserClient;
import cl.gymtastic.loginservice.dto.LoginRequest;
import cl.gymtastic.loginservice.dto.LoginResponse;
import cl.gymtastic.loginservice.dto.UserProfileResponse;
import cl.gymtastic.loginservice.dto.ResetPasswordRequest; // <--- IMPORTANTE: Usar este import

import feign.FeignException;
import jakarta.validation.Valid;
// import jakarta.validation.constraints.NotBlank; // Ya no se necesitan aquí
// import lombok.Data; // Ya no se necesita aquí
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class LoginService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void requestReset(String email) {
        try {
            userClient.requestReset(email);
        } catch (Exception e) {
            System.err.println("Error solicitando reset: " + e.getMessage());
        }
    }

    public boolean confirmReset(ResetPasswordRequest request) {
        try {
            // Log para depurar qué está llegando
            System.out.println("Enviando confirmación para: " + request.getEmail() + " con token: " + request.getToken());
            
            ResponseEntity<Object> response = userClient.confirmReset(request);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // Log para ver el error real
            System.err.println("Error en confirmReset: " + e.getMessage());
            e.printStackTrace(); 
            return false;
        }
    }

    // ... el método login se mantiene igual ...
    public LoginResponse login(@Valid LoginRequest request) {
        
        UserProfileResponse user;
        String email = request.getEmail().trim().toLowerCase();

        // 1. Buscar al usuario en user-service
        try {
            System.out.println("--- DEBUG LOGIN ---");
            System.out.println("1. Buscando usuario: " + email);
            
            ResponseEntity<UserProfileResponse> response = userClient.buscarPorEmail(email);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                System.out.println("Error: Usuario no encontrado o respuesta vacía");
                return new LoginResponse(false, null, "Credenciales inválidas (Error al buscar)", null);
            }
            user = response.getBody();
            
            System.out.println("2. Usuario encontrado: " + user.getEmail());
            System.out.println("3. Hash recibido de BD: " + user.getPassHash());
            System.out.println("4. Contraseña ingresada (Login): " + request.getPassword());

        } catch (FeignException.NotFound e) {
            return new LoginResponse(false, null, "Credenciales inválidas (Usuario no existe)", null);
        } catch (Exception e) {
            return new LoginResponse(false, null, "Error de comunicación: " + e.getMessage(), null);
        }

        // 2. Verificar la contraseña
        boolean passwordMatch = passwordEncoder.matches(request.getPassword().trim(), user.getPassHash());
        System.out.println("5. ¿Coinciden las contraseñas?: " + passwordMatch);
        System.out.println("-------------------");

        if (passwordMatch) {
            user.setPassHash(null); 
            return new LoginResponse(
                true,
                "fake-jwt-token-" + user.getEmail(),
                "Login exitoso",
                user
            );
        } else {
            return new LoginResponse(false, null, "Credenciales inválidas (Contraseña incorrecta)", null);
        }
    }

    // --- BORRAR LA CLASE INTERNA 'ResetPasswordRequest' DE AQUÍ ---
    // (Ya existe una clase igual y correcta en el paquete .dto)
}