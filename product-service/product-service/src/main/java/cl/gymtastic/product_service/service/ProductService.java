package cl.gymtastic.product_service.service;

import cl.gymtastic.product_service.dto.CartItemDto; // <-- Importar
import cl.gymtastic.product_service.dto.StockDecreaseRequest; // <-- Importar
import cl.gymtastic.product_service.model.Product;
import cl.gymtastic.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Importar

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <-- Importar

@Service
public class ProductService {

    // ... (código existente: productRepository, getAllProducts, etc.) ...
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() { // ... (existente)
        return productRepository.findAll();
    }
    public List<Product> getProductsByType(String tipo) { // ... (existente)
        String tipoBusqueda = tipo.trim().toLowerCase();
        return productRepository.findByTipo(tipoBusqueda);
    }
    public Product createProduct(Product product) { // ... (existente)
        if (product.getTipo() == null || !product.getTipo().equals("plan")) {
            product.setTipo("merch");
        }
        return productRepository.save(product);
    }
    public Optional<Product> updateProduct(Integer id, Product productDetails) { // ... (existente)
        return productRepository.findById(id)
            .map(existingProduct -> {
                existingProduct.setNombre(productDetails.getNombre());
                existingProduct.setDescripcion(productDetails.getDescripcion());
                existingProduct.setPrecio(productDetails.getPrecio());
                existingProduct.setStock(productDetails.getStock());
                existingProduct.setImg(productDetails.getImg());
                existingProduct.setTipo(productDetails.getTipo());
                return productRepository.save(existingProduct);
            });
    }
    public boolean deleteProduct(Integer id) { // ... (existente)
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- AÑADIDO: Lógica transaccional para descontar stock ---
    /**
     * Procesa una lista de items para descontar stock.
     * Es transaccional: si un item falla, hace rollback de todos.
     * @throws InsufficientStockException si algún producto no tiene stock.
     */
    @Transactional
    public void decreaseStock(StockDecreaseRequest request) throws InsufficientStockException {
        List<CartItemDto> merchItems = request.getItems();
        
        // 1. Validar stock de todos los items
        List<Integer> failedProductIds = merchItems.stream()
            .filter(item -> {
                // Llama a la query @Modifying.
                // Si devuelve 0, significa que no se pudo actualizar (no hay stock).
                int rowsAffected = productRepository.tryDecrementStock(item.getProductId(), item.getQty());
                return rowsAffected == 0; // Filtra los que fallaron
            })
            .map(CartItemDto::getProductId)
            .collect(Collectors.toList());

        // 2. Si alguno falló, lanzar excepción (esto causa el @Transactional rollback)
        if (!failedProductIds.isEmpty()) {
            throw new InsufficientStockException("Stock insuficiente para productos: " + failedProductIds);
        }
    }
}

// --- AÑADIDO: Excepción personalizada ---
class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}