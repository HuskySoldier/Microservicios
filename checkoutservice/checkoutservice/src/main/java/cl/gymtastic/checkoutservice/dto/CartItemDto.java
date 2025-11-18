package cl.gymtastic.checkoutservice.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Integer productId; // En product-service usamos Integer
    private int qty;
    private String tipo; // "plan" o "merch" (lo necesitamos para la lógica)
    // No necesitamos el precio para el checkout, solo para el total (que la app ya calculó)
}