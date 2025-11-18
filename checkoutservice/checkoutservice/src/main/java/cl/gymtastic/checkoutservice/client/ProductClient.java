package cl.gymtastic.checkoutservice.client;

// --- IMPORTAR LOS DTOs NECESARIOS ---
import cl.gymtastic.checkoutservice.dto.StockDecreaseRequest;
// ------------------------------------

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

// --- CLASE INTERNA ELIMINADA ---
// Ya no definimos StockDecreaseRequest aquí

@FeignClient(name = "product-service", url = "${product-service.url}")
public interface ProductClient {

    // Usamos el endpoint que creamos en la Parte 1
    @PostMapping("/products/decrement-stock")
    ResponseEntity<Map<String, String>> decreaseStock(@RequestBody StockDecreaseRequest request);
}