package cl.gymtastic.product_service.service;

import cl.gymtastic.product_service.model.Product;
import cl.gymtastic.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Lógica de negocio (en este caso, simple re-mapeo)
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Obtiene todos los productos (merch y planes).
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Obtiene productos filtrados por tipo ("plan" o "merch").
     */
    public List<Product> getProductsByType(String tipo) {
        // Valida o normaliza la entrada si es necesario
        String tipoBusqueda = tipo.trim().toLowerCase();
        return productRepository.findByTipo(tipoBusqueda);
    }
    
    // (Aquí irían las funciones de Admin: saveProduct, deleteProduct)
}