package cl.gymtastic.bookingsservice.repository;

import cl.gymtastic.bookingsservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserEmailOrderByFechaHoraDesc(String userEmail);
    
    // --- NUEVO: Buscar por Trainer ---
    List<Booking> findByTrainerIdOrderByFechaHoraAsc(Long trainerId);
}