package cl.gymtastic.trainersservice.repository;

import cl.gymtastic.trainersservice.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// La PK (ID) de Trainer es Long
@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    // JpaRepository ya nos da:
    // - findAll()
    // - findById(Long id)
    // - save(Trainer trainer)
    // - deleteById(Long id)
    // - existsById(Long id)
}