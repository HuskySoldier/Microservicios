package cl.gymtastic.checkoutservice.repository;

import cl.gymtastic.checkoutservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Busca por email y ordena por fecha descendente (lo más nuevo primero)
    List<Order> findByUserEmailOrderByDateDesc(String userEmail);
}