package cl.gymtastic.bookingsservice.service;

import cl.gymtastic.bookingsservice.model.Booking;
import cl.gymtastic.bookingsservice.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @InjectMocks
    private BookingService service;

    private final String TEST_EMAIL = "user@test.cl";

    @Test
    void getMyBookings_Success() {
        // Arrange
        Booking booking1 = Booking.builder().id(1L).userEmail(TEST_EMAIL).build();
        List<Booking> mockList = Arrays.asList(booking1);
        when(repository.findByUserEmailOrderByFechaHoraDesc(TEST_EMAIL)).thenReturn(mockList);

        // Act
        List<Booking> result = service.getMyBookings(TEST_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findByUserEmailOrderByFechaHoraDesc(TEST_EMAIL);
    }

    @Test
    void createBooking_SetsPendingStatus() {
        // Arrange
        Booking newBooking = Booking.builder()
                .userEmail(TEST_EMAIL)
                .trainerId(5L)
                .fechaHora(System.currentTimeMillis())
                .build();
        
        // Mock: Simula que save() devuelve el objeto que se le pasó
        when(repository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking result = service.createBooking(newBooking);

        // Assert
        assertNotNull(result);
        // Verifica la lógica de negocio: el estado debe ser "pendiente"
        assertEquals("pendiente", result.getEstado());
        verify(repository, times(1)).save(newBooking);
    }
}