package cl.gymtastic.attendanceservice.repository;

import cl.gymtastic.attendanceservice.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    // Buscar historial por usuario
    List<Attendance> findByUserEmailOrderByTimestampDesc(String userEmail);

    // Buscar el último check-in abierto (sin checkout)
    @Query("SELECT a FROM Attendance a WHERE a.userEmail = :email AND a.checkOutTimestamp IS NULL ORDER BY a.timestamp DESC LIMIT 1")
    Optional<Attendance> findLastOpen(String email);
}