package cl.gymtastic.loginservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

// DTO para la solicitud que llega a ESTE servicio (/login)
@Data
@Schema(name = "LoginRequest", description = "Datos para iniciar sesión")
public class LoginRequest {
    
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    @Schema(description = "Email del usuario", example = "test@duoc.cl")
    private String email;
    
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Schema(description = "Contraseña", example = "test1234")
    private String password;
}

// DTO para la respuesta de ESTE servicio (/login)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "LoginResponse", description = "Respuesta al iniciar sesión")
public class LoginResponse {
    @Schema(description = "Indica si el login fue exitoso")
    private boolean success;
    
    @Schema(description = "Token JWT o simulado", example = "fake-jwt-token-...")
    private String token;
    
    @Schema(description = "Mensaje de estado", example = "Login exitoso")
    private String message;
    
    @Schema(description = "Datos del perfil del usuario (si el login es exitoso)")
    private UserProfileResponse user; // Devolvemos el perfil
}

// --- DTO para el Cliente Feign ---

// DTO que representa la respuesta que esperamos de user-service
// Debe coincidir con el UserProfileResponse de user-service
@Data
@Schema(name = "UserProfileResponse", description = "Datos del perfil (desde user-service)")
public class UserProfileResponse {
    private String email;
    private String nombre;
    private String rol;
    private String passHash; // ¡Clave! Necesitamos esto para comparar
    
    // Perfil completo (coincide con UserProfileResponse de user-service)
    private Long planEndMillis;
    private Integer sedeId;
    private String sedeName;
    private Double sedeLat;
    private Double sedeLng;
    private String avatarUri;
    private String fono;
    private String bio;
}