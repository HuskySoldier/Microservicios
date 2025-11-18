package cl.gymtastic.product_service.repository;

import cl.gymtastic.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // <-- Importar
import org.springframework.data.jpa.repository.Query; // <-- Importar
import org.springframework.data.repository.query.Param; // <-- Importar
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    List<Product> findByTipo(String tipo);

    // --- AÑADIDO: Descontar stock de forma segura ---
    /**
     * Intenta descontar el stock de un producto.
     * Solo descuenta si el tipo es 'merch' Y el stock es suficiente.
     * @return El número de filas afectadas (1 si tuvo éxito, 0 si falló).
     */
    @Modifying // Indica que esta query modifica datos
    @Query("UPDATE Product p SET p.stock = p.stock - :qty " +
           "WHERE p.id = :id AND p.tipo = 'merch' AND p.stock >= :qty")
    int tryDecrementStock(@Param("id") Integer id, @Param("qty") int qty);
}