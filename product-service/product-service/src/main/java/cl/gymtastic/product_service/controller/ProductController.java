package cl.gymtastic.product_service.controller;

import cl.gymtastic.product_service.dto.StockDecreaseRequest;
import cl.gymtastic.product_service.model.Product;
import cl.gymtastic.product_service.service.InsufficientStockException;
import cl.gymtastic.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@Tag(name = "Product Service", description = "Endpoints para gestión de Productos y Stock")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    // --- CRUD BÁSICO (Admin y Lectura) ---

    @Operation(summary = "Obtener todos los productos")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Obtener productos por tipo (plan/merch)")
    @GetMapping("/{tipo}")
    public ResponseEntity<List<Product>> getProductsByType(@PathVariable String tipo) {
        List<Product> products = productService.getProductsByType(tipo);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Crear un nuevo producto (Admin)")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product newProduct = productService.createProduct(product);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Actualizar un producto existente (Admin)")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails)
                .map(updatedProduct -> ResponseEntity.ok(updatedProduct))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Eliminar un producto (Admin)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // --- LÓGICA DE STOCK (Para Checkout Service) ---
    
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