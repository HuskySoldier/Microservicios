package cl.gymtastic.product_service.service;

import cl.gymtastic.product_service.dto.CartItemDto;
import cl.gymtastic.product_service.dto.StockDecreaseRequest;
import cl.gymtastic.product_service.model.Product;
import cl.gymtastic.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // --- (CRUD BÁSICO) ---

    /** Obtiene todos los productos. */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    /** Obtiene productos por tipo ("plan" o "merch"). */
    public List<Product> getProductsByType(String tipo) {
        String tipoBusqueda = tipo.trim().toLowerCase();
        return productRepository.findByTipo(tipoBusqueda);
    }
    
    /** Crea un nuevo producto (usado por Admin). */
    public Product createProduct(Product product) {
        // Asegura que el tipo sea 'merch' si no es 'plan'
        if (product.getTipo() == null || !product.getTipo().trim().equalsIgnoreCase("plan")) {
            product.setTipo("merch");
        }
        return productRepository.save(product);
    }
    
    /** Actualiza un producto existente (usado por Admin). */
    public Optional<Product> updateProduct(Integer id, Product productDetails) {
        return productRepository.findById(id)
            .map(existingProduct -> {
                // Actualiza solo los campos que deberían ser editables
                existingProduct.setNombre(productDetails.getNombre());
                existingProduct.setDescripcion(productDetails.getDescripcion());
                existingProduct.setPrecio(productDetails.getPrecio());
                existingProduct.setStock(productDetails.getStock());
                // El tipo y la imagen también deben ser actualizables por el Admin
                existingProduct.setImg(productDetails.getImg());
                existingProduct.setTipo(productDetails.getTipo());
                
                return productRepository.save(existingProduct);
            });
    }
    
    /** Elimina un producto por ID (usado por Admin). */
    public boolean deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- (LÓGICA DE STOCK PARA CHECKOUT) ---
    
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

