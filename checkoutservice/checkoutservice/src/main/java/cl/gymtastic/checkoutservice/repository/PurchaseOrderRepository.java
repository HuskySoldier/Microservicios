package cl.gymtastic.checkoutservice.repository;

import cl.gymtastic.checkoutservice.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    // Ahora Spring Data buscará en la entidad 'PurchaseOrder' y no habrá confusión
    List<PurchaseOrder> findByUserEmailOrderByDateDesc(String userEmail);
}