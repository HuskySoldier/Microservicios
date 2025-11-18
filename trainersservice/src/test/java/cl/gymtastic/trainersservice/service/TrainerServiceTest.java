package cl.gymtastic.trainersservice.service;

import cl.gymtastic.trainersservice.model.Trainer;
import cl.gymtastic.trainersservice.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer mockTrainer;

    @BeforeEach
    void setUp() {
        mockTrainer = Trainer.builder()
                .id(1L)
                .nombre("Ana Pérez")
                .especialidad("Funcional")
                .email("ana@gymtastic.cl")
                .fono("+56911111111")
                .build();
    }

    @Test
    void createTrainer_Success() {
        // Arrange
        Trainer newTrainer = Trainer.builder().nombre("Nuevo").especialidad("Yoga").build();
        when(trainerRepository.save(any(Trainer.class))).thenReturn(mockTrainer);

        // Act
        Trainer result = trainerService.createTrainer(newTrainer);

        // Assert
        assertNotNull(result);
        verify(trainerRepository, times(1)).save(newTrainer);
    }

    @Test
    void updateTrainer_Success() {
        // Arrange
        Trainer updateDetails = new Trainer();
        updateDetails.setNombre("Ana Pérez Actualizada");
        updateDetails.setEspecialidad("Pilates");

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(mockTrainer));
        // Simular que save() devuelve el objeto actualizado
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Trainer> result = trainerService.updateTrainer(1L, updateDetails);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Pilates", result.get().getEspecialidad());
        verify(trainerRepository, times(1)).save(mockTrainer);
    }

    @Test
    void deleteTrainer_Success() {
        // Arrange
        when(trainerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(trainerRepository).deleteById(1L);

        // Act
        boolean result = trainerService.deleteTrainer(1L);

        // Assert
        assertTrue(result);
        verify(trainerRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void deleteTrainer_Failure_NotFound() {
        // Arrange
        when(trainerRepository.existsById(2L)).thenReturn(false);

        // Act
        boolean result = trainerService.deleteTrainer(2L);

        // Assert
        assertFalse(result);
        verify(trainerRepository, never()).deleteById(anyLong());
    }
}