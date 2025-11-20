package cl.gymtastic.checkoutservice.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Integer productId;
    
    // Cambiamos 'qty' a 'cantidad' para coincidir con tu CheckoutService
    // OJO: Asegúrate de que tu App Android envíe este campo como "cantidad" en el JSON, 
    // o usa @JsonProperty("qty") si quieres mantener el JSON viejo.
    private int cantidad; 
    
    private String tipo; // "plan" o "merch"

    // --- NUEVOS CAMPOS NECESARIOS PARA EL HISTORIAL ---
    // Estos son necesarios para guardar la descripción (ej: "Proteina Whey") y calcular el total.
    private String nombre; 
    private Double precio; 
}