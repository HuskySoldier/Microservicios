package cl.gymtastic.checkoutservice.dto;

import lombok.Data;

@Data
public class SubscriptionUpdateRequest {
    // Coincide con el DTO que creamos en user-service
    private Long planEndMillis;
    private Integer sedeId;
    private String sedeName;
    private Double sedeLat;
    private Double sedeLng;
}