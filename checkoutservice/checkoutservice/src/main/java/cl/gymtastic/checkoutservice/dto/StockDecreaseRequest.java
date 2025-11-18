package cl.gymtastic.checkoutservice.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Este DTO es el cuerpo (body) que se enviará al product-service
 * en la petición POST /products/decrement-stock.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDecreaseRequest {
    private List<CartItemDto> items;
}