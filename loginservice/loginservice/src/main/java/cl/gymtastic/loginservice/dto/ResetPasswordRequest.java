package cl.gymtastic.loginservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank 
    private String email;
    
    @NotBlank 
    private String token;
    
    @NotBlank 
    private String newPassword;
}