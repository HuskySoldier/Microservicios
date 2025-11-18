package cl.gymtastic.product_service.dto;

import lombok.Data;
import java.util.List;

// DTO para recibir la petición de descontar stock
@Data
public class StockDecreaseRequest {
    private List<CartItemDto> items;
}