package cl.gymtastic.product_service.controller;

import cl.gymtastic.product_service.model.Product;
import cl.gymtastic.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // <-- Importar
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

    // === GET (Leer Todos) ===
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

    // === GET (Leer por Tipo) ===
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

    // --- AÑADIDO: POST (Crear) ---
    @Operation(summary = "Crear un nuevo producto (Admin)")
    @ApiResponse(responseCode = "201", description = "Producto creado")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product newProduct = productService.createProduct(product);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED); // Devuelve 201 Created
    }

    // --- AÑADIDO: PUT (Actualizar) ---
    @Operation(summary = "Actualizar un producto existente (Admin)")
    @ApiResponse(responseCode = "200", description = "Producto actualizado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails)
                .map(updatedProduct -> ResponseEntity.ok(updatedProduct)) // Devuelve 200 OK
                .orElse(ResponseEntity.notFound().build()); // Devuelve 404 Not Found
    }

    // --- AÑADIDO: DELETE (Eliminar) ---
    @Operation(summary = "Eliminar un producto (Admin)")
    @ApiResponse(responseCode = "204", description = "Producto eliminado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build(); // Devuelve 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Devuelve 404 Not Found
        }
    }
}