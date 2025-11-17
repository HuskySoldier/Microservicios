package cl.gymtastic.userservice.repository;

import cl.gymtastic.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// La PK (ID) de User es String (email)
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * Busca un usuario por su email (alternativa a findById).
     * Spring Data JPA crea la query automáticamente.
     */
    Optional<User> findByEmail(String email);
}