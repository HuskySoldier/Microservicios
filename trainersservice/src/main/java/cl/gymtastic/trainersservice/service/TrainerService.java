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

    public Trainer createTrainer(Trainer trainer) {
        // save() crea un nuevo registro si el ID es nulo o 0
        return trainerRepository.save(trainer);
    }

    public Optional<Trainer> updateTrainer(Long id, Trainer trainerDetails) {
        return trainerRepository.findById(id)
            .map(existingTrainer -> {
                existingTrainer.setNombre(trainerDetails.getNombre());
                existingTrainer.setEspecialidad(trainerDetails.getEspecialidad());
                existingTrainer.setFono(trainerDetails.getFono());
                existingTrainer.setEmail(trainerDetails.getEmail());
                existingTrainer.setImg(trainerDetails.getImg());
                return trainerRepository.save(existingTrainer);
            });
    }

    public boolean deleteTrainer(Long id) {
        if (trainerRepository.existsById(id)) {
            trainerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}