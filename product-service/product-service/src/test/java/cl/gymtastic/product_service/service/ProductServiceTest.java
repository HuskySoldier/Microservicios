package cl.gymtastic.product_service.service;

import cl.gymtastic.product_service.dto.CartItemDto;
import cl.gymtastic.product_service.dto.StockDecreaseRequest;
import cl.gymtastic.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    // --- PRUEBA DE DESCUENTO DE STOCK (TRANSACCIONAL) ---

    @Test
    void decreaseStock_Success() {
        // Arrange: Dos items con stock suficiente
        CartItemDto item1 = new CartItemDto();
        item1.setProductId(101);
        item1.setQty(2);

        CartItemDto item2 = new CartItemDto();
        item2.setProductId(102);
        item2.setQty(1);

        StockDecreaseRequest request = new StockDecreaseRequest();
        request.setItems(Arrays.asList(item1, item2));

        // Mock: Simula que ambas llamadas a tryDecrementStock devuelven 1 (éxito)
        when(productRepository.tryDecrementStock(101, 2)).thenReturn(1);
        when(productRepository.tryDecrementStock(102, 1)).thenReturn(1);

        // Act & Assert: No debe lanzar excepción
        assertDoesNotThrow(() -> productService.decreaseStock(request));

        // Verify: Verificar que se llamó a la base de datos para ambos items
        verify(productRepository, times(1)).tryDecrementStock(101, 2);
        verify(productRepository, times(1)).tryDecrementStock(102, 1);
    }

    @Test
    void decreaseStock_Failure_InsufficientStock() {
        // Arrange: item1 OK, item2 falla (stock insuficiente)
        CartItemDto item1 = new CartItemDto();
        item1.setProductId(201);
        item1.setQty(3);

        CartItemDto item2 = new CartItemDto();
        item2.setProductId(202);
        item2.setQty(5); 

        StockDecreaseRequest request = new StockDecreaseRequest();
        request.setItems(Arrays.asList(item1, item2));

        // Mock: item1 exitoso (devuelve 1), item2 falla (devuelve 0)
        when(productRepository.tryDecrementStock(201, 3)).thenReturn(1);
        when(productRepository.tryDecrementStock(202, 5)).thenReturn(0);

        // Act & Assert: Debe lanzar InsufficientStockException
        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> {
            productService.decreaseStock(request);
        });

        // Verify:
        // 1. Verificar que se llamó a la base de datos para ambos items
        verify(productRepository, times(1)).tryDecrementStock(201, 3);
        verify(productRepository, times(1)).tryDecrementStock(202, 5);
        
        // 2. Verificar el mensaje de la excepción (debe indicar el ID del que falló)
        assertTrue(exception.getMessage().contains("Stock insuficiente para productos: [202]"));
    }
}