package cl.gymtastic.userservice.dto;

import lombok.Data;

// DTO para que un admin cambie el rol de un usuario
@Data
public class AdminRoleUpdateRequest {
    private String rol;
}