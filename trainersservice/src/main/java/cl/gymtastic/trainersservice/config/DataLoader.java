package cl.gymtastic.trainersservice.config;

import cl.gymtastic.trainersservice.model.Trainer;
import cl.gymtastic.trainersservice.repository.TrainerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(TrainerRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                System.out.println("Poblando base de datos con trainers iniciales...");
                
                // URIs de imagen (pueden ser las mismas de la app para consistencia)
                String anaImgUri = "android.resource://cl.gymtastic.app/drawable/trainer_ana";
                String luisImgUri = "android.resource://cl.gymtastic.app/drawable/trainer_luis";

                repository.saveAll(List.of(
                    Trainer.builder()
                        .nombre("Ana Pérez")
                        .fono("+56911111111")
                        .email("ana@gymtastic.cl")
                        .especialidad("Funcional")
                        .img(anaImgUri)
                        .build(),
                    Trainer.builder()
                        .nombre("Luis Gómez")
                        .fono("+56922222222")
                        .email("luis@gymtastic.cl")
                        .especialidad("Hipertrofia")
                        .img(luisImgUri)
                        .build()
                ));
                System.out.println("--> Base de datos poblada con Trainers.");
            }
        };
    }
}