package cl.gymtastic.userservice.config;

import cl.gymtastic.userservice.model.User;
import cl.gymtastic.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader {

    // Inyectamos el encriptador de contraseñas
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            
            // --- Seed Usuarios ---
            if (userRepository.count() == 0) {
                System.out.println("Poblando base de datos con usuarios iniciales...");
                userRepository.saveAll(List.of(
                    User.builder()
                        .email("admin@gymtastic.cl")
                        // Usamos BCrypt (igual que tu referencia)
                        .passHash(passwordEncoder.encode("admin123")) 
                        .nombre("Administrador")
                        .rol("admin")
                        .build(),
                    User.builder()
                        .email("test@gymtastic.cl")
                        .passHash(passwordEncoder.encode("test1234"))
                        .nombre("Usuario Test")
                        .rol("user")
                        .build()
                ));
                System.out.println("--> Base de datos poblada con Usuarios (BCrypt).");
            } else {
                System.out.println("--> La tabla 'users' ya tiene datos.");
            }
        };
    }
}