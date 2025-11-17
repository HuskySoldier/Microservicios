package cl.gymtastic.userservice.dto;

import cl.gymtastic.userservice.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// (En un proyecto real, cada DTO iría en su propio archivo)

// --- DTO para Registro (Microservicio 1) ---
@Data
public class RegisterRequest {
    @NotBlank @Email
    private String email;
    
    @NotBlank @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    @NotBlank
    private String nombre;
}

// --- DTO para Login (Microservicio 2) ---
@Data
public class LoginRequest {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
}

@Data
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String token;
    private String message;
    private UserProfileResponse user; // Devolvemos el perfil al loguear
}

// --- DTO para Perfil de Usuario (Microservicio 3) ---
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    // DTO que enviamos al cliente, excluyendo el passHash
    private String email;
    private String nombre;
    private String rol;
    private Long planEndMillis;
    private Integer sedeId;
    private String sedeName;
    private Double sedeLat;
    private Double sedeLng;
    private String avatarUri;
    private String fono;
    private String bio;

    // Constructor para mapear fácil desde la Entidad User
    public UserProfileResponse(User user) {
        this.email = user.getEmail();
        this.nombre = user.getNombre();
        this.rol = user.getRol();
        this.planEndMillis = user.getPlanEndMillis();
        this.sedeId = user.getSedeId();
        this.sedeName = user.getSedeName();
        this.sedeLat = user.getSedeLat();
        this.sedeLng = user.getSedeLng();
        this.avatarUri = user.getAvatarUri();
        this.fono = user.getFono();
        this.bio = user.getBio();
    }
}