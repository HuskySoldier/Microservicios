package cl.gymtastic.userservice.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String token;
    private String message;
    private UserProfileResponse user; // Devolvemos el perfil al loguear
}