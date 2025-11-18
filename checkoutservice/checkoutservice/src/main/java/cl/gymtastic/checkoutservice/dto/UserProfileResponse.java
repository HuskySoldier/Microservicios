package cl.gymtastic.checkoutservice.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String email;
    private String nombre;
    private String rol;
    private Long planEndMillis; // <-- Esto es lo que necesitamos
    // ... (el resto de campos no son necesarios para la lógica de checkout)
}