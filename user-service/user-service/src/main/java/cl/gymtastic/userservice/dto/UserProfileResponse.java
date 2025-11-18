package cl.gymtastic.userservice.dto;

import cl.gymtastic.userservice.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
    private String passHash;

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
        this.passHash = user.getPassHash();
    }
}