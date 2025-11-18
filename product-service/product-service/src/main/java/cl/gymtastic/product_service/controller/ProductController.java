package cl.gymtastic.product_service.controller;

// ... (Importaciones existentes)
import cl.gymtastic.product_service.dto.StockDecreaseRequest; // <-- Importar
import cl.gymtastic.product_service.model.Product;
import cl.gymtastic.product_service.service.InsufficientStockException; // <-- Importar
import cl.gymtastic.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // <-- Importar Map

@RestController
@RequestMapping("/products")
// ... (Anotaciones existentes)
public class ProductController {

    @Autowired
    private ProductService productService;

    // ... (Endpoints existentes: GET, GET/{tipo}, POST, PUT, DELETE) ...
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() { // ... (existente)
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
    @GetMapping("/{tipo}")
    public ResponseEntity<List<Product>> getProductsByType(@PathVariable String tipo) { // ... (existente)
        List<Product> products = productService.getProductsByType(tipo);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) { // ... (existente)
        Product newProduct = productService.createProduct(product);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody Product productDetails) { // ... (existente)
        return productService.updateProduct(id, productDetails)
                .map(updatedProduct -> ResponseEntity.ok(updatedProduct))
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) { // ... (existente)
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // --- AÑADIDO: Endpoint para descontar stock ---
    @Operation(summary = "Descontar stock de productos (Checkout)")
    @ApiResponse(responseCode = "200", description = "Stock descontado")
    @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    @PostMapping("/decrement-stock")
    public ResponseEntity<?> decreaseStock(@RequestBody StockDecreaseRequest request) {
        try {
            productService.decreaseStock(request);
            return ResponseEntity.ok(Map.of("message", "Stock descontado exitosamente"));
        } catch (InsufficientStockException e) {
            // 409 Conflict es apropiado para "stock insuficiente"
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }
}