package cl.gymtastic.product_service.service;

import cl.gymtastic.product_service.model.Product;
import cl.gymtastic.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // <-- Importar Optional

// Lógica de negocio
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
        String tipoBusqueda = tipo.trim().toLowerCase();
        return productRepository.findByTipo(tipoBusqueda);
    }

    // --- AÑADIDO: Guardar un nuevo producto ---
    /**
     * Guarda un nuevo producto en la base de datos.
     * El ID se genera automáticamente.
     * @param product El producto a crear (sin ID)
     * @return El producto guardado (con ID)
     */
    public Product createProduct(Product product) {
        // Aseguramos que el tipo sea "merch" si viene de la app admin
        if (product.getTipo() == null || !product.getTipo().equals("plan")) {
            product.setTipo("merch");
        }
        // save() crea un nuevo registro si el ID es nulo o 0
        return productRepository.save(product);
    }

    // --- AÑADIDO: Actualizar un producto existente ---
    /**
     * Actualiza un producto existente por su ID.
     * @param id El ID del producto a actualizar
     * @param productDetails Los nuevos datos del producto
     * @return Un Optional con el producto actualizado, o vacío si no se encontró
     */
    public Optional<Product> updateProduct(Integer id, Product productDetails) {
        // map() solo se ejecuta si findById() encuentra algo
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

    // --- AÑADIDO: Eliminar un producto por ID ---
    /**
     * Elimina un producto por su ID.
     * @param id El ID del producto a eliminar
     * @return true si se eliminó, false si no se encontró
     */
    public boolean deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        // No se encontró el producto
        return false;
    }
}