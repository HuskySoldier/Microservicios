package cl.gymtastic.userservice.dto;

import lombok.Data;

// DTO para que un usuario actualice su propio perfil
@Data
public class ProfileUpdateRequest {
    private String nombre;
    private String fono;
    private String bio;
    private String avatarUri;
}