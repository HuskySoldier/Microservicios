package cl.gymtastic.loginservice.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

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
