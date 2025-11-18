package cl.gymtastic.userservice.dto;

import lombok.Data;

// DTO para actualizar la suscripción de un usuario (desde checkout-service)
@Data
public class SubscriptionUpdateRequest {
    private Long planEndMillis;
    private Integer sedeId;
    private String sedeName;
    private Double sedeLat;
    private Double sedeLng;
}