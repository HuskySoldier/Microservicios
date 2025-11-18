package cl.gymtastic.bookingsservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private Long trainerId;

    @Column(nullable = false)
    private Long fechaHora; // Timestamp de la reserva

    @Column(nullable = false)
    private String estado; // "pendiente", "confirmada", etc.
}