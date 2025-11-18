package cl.gymtastic.trainersservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Trainer", description = "Entidad que representa a un entrenador")
public class Trainer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del trainer", example = "1")
    private Long id; // Coincide con el Long de TrainerEntity.kt

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Nombre del trainer", example = "Ana Pérez")
    private String nombre;
    
    @NotBlank(message = "El fono no puede estar vacío")
    @Schema(description = "Fono de contacto", example = "+56911111111")
    private String fono;
    
    @Email(message = "Debe ser un email válido")
    @Column(unique = true) // Generalmente el email es único
    @Schema(description = "Email de contacto", example = "ana@gymtastic.cl")
    private String email;
    
    @NotBlank(message = "La especialidad no puede estar vacía")
    @Schema(description = "Especialidad principal", example = "Funcional")
    private String especialidad;
    
    @Column(length = 512)
    @Schema(description = "URL o URI de la imagen", example = "android.resource://...")
    private String img;
}