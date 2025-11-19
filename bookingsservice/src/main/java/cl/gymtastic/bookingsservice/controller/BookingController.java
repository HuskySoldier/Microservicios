package cl.gymtastic.bookingsservice.controller;

import cl.gymtastic.bookingsservice.model.Booking;
import cl.gymtastic.bookingsservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Booking Service")
@CrossOrigin
public class BookingController {

    @Autowired
    private BookingService service;

    @Operation(summary = "Obtener reservas de un usuario")
    @GetMapping("/user/{email}")
    public ResponseEntity<List<Booking>> getMyBookings(@PathVariable String email) {
        return ResponseEntity.ok(service.getMyBookings(email));
    }

    @Operation(summary = "Crear una nueva reserva")
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBooking(booking));
    }

    @Operation(summary = "Obtener reservas asignadas a un trainer")
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<Booking>> getTrainerBookings(@PathVariable Long trainerId) {
        return ResponseEntity.ok(service.getTrainerBookings(trainerId));
    }
}