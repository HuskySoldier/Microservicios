package cl.gymtastic.trainersservice.service;

import cl.gymtastic.trainersservice.model.Trainer;
import cl.gymtastic.trainersservice.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    @Autowired
    private TrainerRepository trainerRepository;

    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    /** Crea un nuevo trainer (POST). */
    public Trainer createTrainer(Trainer trainer) {
        // Asegura que el ID sea nulo para forzar la creación de uno nuevo
        trainer.setId(null); 
        return trainerRepository.save(trainer);
    }

    /** Actualiza un trainer existente (PUT). */
    public Optional<Trainer> updateTrainer(Long id, Trainer trainerDetails) {
        return trainerRepository.findById(id)
            .map(existingTrainer -> {
                // Actualiza solo los campos que deben ser editables
                existingTrainer.setNombre(trainerDetails.getNombre());
                existingTrainer.setEspecialidad(trainerDetails.getEspecialidad());
                existingTrainer.setFono(trainerDetails.getFono());
                existingTrainer.setEmail(trainerDetails.getEmail());
                existingTrainer.setImg(trainerDetails.getImg());
                return trainerRepository.save(existingTrainer);
            });
    }

    /** Elimina un trainer (DELETE). */
    public boolean deleteTrainer(Long id) {
        if (trainerRepository.existsById(id)) {
            trainerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}