package cl.gymtastic.checkoutservice.dto;

import lombok.Data;
import java.util.List;

// Lo que la App envía a ESTE servicio
@Data
public class CheckoutRequest {
    private String userEmail;
    private List<CartItemDto> items;
    private SedeDto sede; // Opcional, solo si hay un plan
}