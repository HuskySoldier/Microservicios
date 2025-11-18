package cl.gymtastic.trainersservice.controller;

import cl.gymtastic.trainersservice.model.Trainer;
import cl.gymtastic.trainersservice.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainers") // Ruta base
@Tag(name = "Trainer Service", description = "Endpoints para gestionar Trainers")
@CrossOrigin // Permite llamadas desde tu app móvil
public class TrainerController {

    @Autowired
    private TrainerService trainerService;

    @Operation(summary = "Obtener todos los trainers")
    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers() {
        List<Trainer> trainers = trainerService.getAllTrainers();
        if (trainers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(trainers);
    }

    @Operation(summary = "Crear un nuevo trainer (Admin)")
    @PostMapping
    public ResponseEntity<Trainer> createTrainer(@RequestBody Trainer trainer) {
        Trainer newTrainer = trainerService.createTrainer(trainer);
        return new ResponseEntity<>(newTrainer, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un trainer existente (Admin)")
    @PutMapping("/{id}")
    public ResponseEntity<Trainer> updateTrainer(@PathVariable Long id, @RequestBody Trainer trainerDetails) {
        return trainerService.updateTrainer(id, trainerDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un trainer (Admin)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrainer(@PathVariable Long id) {
        if (trainerService.deleteTrainer(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}