package cl.gymtastic.loginservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
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
