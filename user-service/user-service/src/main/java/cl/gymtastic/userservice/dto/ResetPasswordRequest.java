package cl.gymtastic.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank private String email;
    @NotBlank private String token;
    @NotBlank private String newPassword;
}