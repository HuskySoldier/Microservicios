package cl.gymtastic.attendanceservice.service;

import cl.gymtastic.attendanceservice.model.Attendance;
import cl.gymtastic.attendanceservice.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
// [LINEA ELIMINADA] import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {

    @Mock
    private AttendanceRepository repository;

    @InjectMocks
    private AttendanceService service;

    private final String TEST_EMAIL = "user@test.cl";
    private Attendance openAttendance;

    @BeforeEach
    void setUp() {
        openAttendance = Attendance.builder()
                .id(1L)
                .userEmail(TEST_EMAIL)
                .timestamp(System.currentTimeMillis() - 3600000) // Hace 1 hora
                .checkOutTimestamp(null)
                .build();
    }

    // --- PRUEBAS DE CHECK-IN ---

    @Test
    @SuppressWarnings("null") // <-- CORRECCIÓN: Suprime las advertencias de nulidad en la asignación del resultado
    void checkIn_Success_NewSession() {
        // Arrange: Simula que no hay sesión abierta
        when(repository.findLastOpen(TEST_EMAIL)).thenReturn(Optional.empty());
        when(repository.save(any(Attendance.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Attendance result = service.checkIn(TEST_EMAIL);

        // Assert
        assertNotNull(result);
        assertNull(result.getCheckOutTimestamp());
        // Verifica que se llamó a save() para crear una nueva entrada
        verify(repository, times(1)).save(any(Attendance.class));
    }

    @Test
    @SuppressWarnings("null") // <-- CORRECCIÓN: Suprime las advertencias de nulidad en la asignación del resultado
    void checkIn_AlreadyOpen() {
        // Arrange: Simula que ya hay una sesión abierta
        when(repository.findLastOpen(TEST_EMAIL)).thenReturn(Optional.of(openAttendance));

        // Act
        Attendance result = service.checkIn(TEST_EMAIL);

        // Assert: Debe devolver la sesión existente (sin crear una nueva)
        assertNotNull(result);
        assertEquals(openAttendance.getId(), result.getId());
        // Verifica que NUNCA se llamó a save() para crear una nueva entrada
        verify(repository, never()).save(any(Attendance.class));
    }

    // --- PRUEBAS DE CHECK-OUT ---

    @Test
    @SuppressWarnings("null") // <-- CORRECCIÓN: Suprime las advertencias de nulidad en la asignación del resultado
    void checkOut_Success() {
        // Arrange: Encuentra la sesión abierta
        when(repository.findLastOpen(TEST_EMAIL)).thenReturn(Optional.of(openAttendance));
        when(repository.save(any(Attendance.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Attendance result = service.checkOut(TEST_EMAIL);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCheckOutTimestamp());
        
        // Verify: Se llamó a save() para actualizar el objeto
        verify(repository, times(1)).save(openAttendance);
    }

    @SuppressWarnings("null")
    @Test
    void checkOut_Failure_NoOpenSession() {
        // Arrange: No hay sesión abierta
        when(repository.findLastOpen(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert: Debe lanzar la excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.checkOut(TEST_EMAIL);
        });

        assertEquals("No hay sesión activa para cerrar.", exception.getMessage());
        verify(repository, never()).save(any(Attendance.class));
    }
}