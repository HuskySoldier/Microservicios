package cl.gymtastic.loginservice.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

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
