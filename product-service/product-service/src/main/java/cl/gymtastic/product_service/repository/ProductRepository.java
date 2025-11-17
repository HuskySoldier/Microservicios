package cl.gymtastic.product_service.repository;

import cl.gymtastic.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    /**
     * Busca productos por su columna 'tipo'.
     * Spring Data JPA crea la query automáticamente a partir del nombre del método.
     */
    List<Product> findByTipo(String tipo);
}