package cl.gymtastic.bookingsservice.service;

import cl.gymtastic.bookingsservice.model.Booking;
import cl.gymtastic.bookingsservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository repository;

    public List<Booking> getMyBookings(String email) {
        return repository.findByUserEmailOrderByFechaHoraDesc(email);
    }

    public Booking createBooking(Booking booking) {
        booking.setEstado("pendiente");
        return repository.save(booking);
    }
} 