package cl.gymtastic.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "users") // 'user' es palabra reservada en MySQL, 'users' es más seguro
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "User", description = "Entidad que representa un usuario y su perfil")
public class User {
    
    @Id
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email no puede estar vacío")
    @Column(length = 255, nullable = false, unique = true)
    @Schema(description = "Email del usuario (PK)", example = "admin@gymtastic.cl")
    private String email; // Clave primaria

    @NotBlank(message = "El hash de la contraseña no puede estar vacío")
    @Column(nullable = false, length = 1024)
    @Schema(description = "Hash de la contraseña (BCrypt)", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String passHash;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Nombre del usuario", example = "Administrador")
    private String nombre;
    
    @NotBlank(message = "El rol no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Rol del usuario", example = "admin")
    private String rol;

    // --- Campos de Suscripción (nullables) ---
    @Schema(description = "Timestamp (millis) de fin de plan", example = "1735689600000")
    private Long planEndMillis;
    
    @Schema(description = "ID de la sede asociada", example = "1")
    private Integer sedeId;
    
    @Schema(description = "Nombre de la sede", example = "Sede Central")
    private String sedeName;
    
    @Schema(description = "Latitud de la sede", example = "-33.44")
    private Double sedeLat;
    
    @Schema(description = "Longitud de la sede", example = "-70.65")
    private Double sedeLng;

    // --- Campos de Perfil (nullables) ---
    @Column(length = 512)
    @Schema(description = "URI/URL de la foto de perfil", example = "file://...")
    private String avatarUri;
    
    @Schema(description = "Teléfono del usuario", example = "+56912345678")
    private String fono;
    
    @Column(length = 1024) // Más espacio para la biografía
    @Schema(description = "Biografía del usuario", example = "Entusiasta del fitness...")
    private String bio;
}