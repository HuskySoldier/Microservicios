package cl.gymtastic.product_service.dto;

import lombok.Data;

// DTO que representa un item del carrito
@Data
public class CartItemDto {
    private Integer productId; // Coincide con el ID de Product (Integer)
    private int qty;
    // No necesitamos precio ni tipo aquí, solo descontar stock
}