package cl.gymtastic.userservice.service;

import cl.gymtastic.userservice.dto.LoginRequest;
import cl.gymtastic.userservice.dto.LoginResponse;
import cl.gymtastic.userservice.dto.RegisterRequest;
import cl.gymtastic.userservice.dto.UserProfileResponse;
import cl.gymtastic.userservice.model.User;
import cl.gymtastic.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated // Habilita la validación en los métodos
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectado desde SecurityConfig

    /**
     * Microservicio 1: REGISTER
     * Crea un nuevo usuario.
     */
    public User register(@Valid RegisterRequest request) { // @Valid activa las validaciones del DTO
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsById(normalizedEmail)) {
            throw new RuntimeException("El email ya existe");
        }

        // Hasheamos la contraseña con BCrypt
        String passHash = passwordEncoder.encode(request.getPassword().trim());

        User newUser = User.builder()
            .email(normalizedEmail)
            .passHash(passHash)
            .nombre(request.getNombre().trim())
            .rol("user") // Rol por defecto
            // Todos los demás campos (perfil, plan) quedan null
            .build();
        
        return userRepository.save(newUser);
    }

    /**
     * Microservicio 2: LOGIN
     * Autentica un usuario y devuelve un token (simulado) y perfil.
     */
    public LoginResponse login(@Valid LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(email); // Usa findByEmail
        
        if (userOpt.isEmpty()) {
            return new LoginResponse(false, "", "Credenciales inválidas", null);
        }

        User user = userOpt.get();
        
        // Verificamos la contraseña hasheada con BCrypt
        if (passwordEncoder.matches(request.getPassword().trim(), user.getPassHash())) {
            // Login OK
            UserProfileResponse userDto = new UserProfileResponse(user);
            
            return new LoginResponse(
                true, 
                "fake-jwt-token-" + email, // Token simulado
                "Login exitoso",
                userDto // Devolvemos los datos del usuario
            );
        }
        
        // Contraseña incorrecta
        return new LoginResponse(false, "", "Credenciales inválidas", null);
    }

    /**
     * Microservicio 3: GET USER
     * Obtiene el perfil de un usuario por email.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }
}