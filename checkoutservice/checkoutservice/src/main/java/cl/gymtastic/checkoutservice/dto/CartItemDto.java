package cl.gymtastic.checkoutservice.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty; // <--- IMPORTANTE

@Data
public class CartItemDto {
    private Integer productId;
    
    // Agregamos @JsonProperty para que al enviarse al otro servicio se llame "qty"
    @JsonProperty("qty") 
    private int cantidad; 
    
    private String tipo; 
    private String nombre; 
    private Double precio; 
}