package cl.gymtastic.product_service.controller;

import cl.gymtastic.product_service.model.Product;
import cl.gymtastic.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products") // Ruta base para productos
@Tag(name = "Product Service", description = "Endpoints para gestionar productos y planes")
@CrossOrigin // Permite llamadas desde tu app móvil
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Obtener todos los productos y planes")
    @ApiResponse(responseCode = "200", description = "Lista de productos")
    @ApiResponse(responseCode = "204", description = "No hay productos")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Obtener productos filtrados por tipo (plan o merch)")
    @ApiResponse(responseCode = "200", description = "Lista de productos filtrada")
    @ApiResponse(responseCode = "204", description = "No hay productos de ese tipo")
    @GetMapping("/{tipo}")
    public ResponseEntity<List<Product>> getProductsByType(@PathVariable String tipo) {
        List<Product> products = productService.getProductsByType(tipo);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
}