package cl.gymtastic.userservice.service;

// --- Asegúrate de importar los nuevos DTOs ---
import cl.gymtastic.userservice.dto.*; 
import cl.gymtastic.userservice.model.User;
import cl.gymtastic.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List; // <-- Importar List
import java.util.Optional;

@Service
@Validated
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Microservicio 1: REGISTER
     */
    public User register(@Valid RegisterRequest request) {
        // ... (código existente sin cambios)
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsById(normalizedEmail)) {
            throw new RuntimeException("El email ya existe");
        }
        String passHash = passwordEncoder.encode(request.getPassword().trim());
        User newUser = User.builder()
            .email(normalizedEmail)
            .passHash(passHash)
            .nombre(request.getNombre().trim())
            .rol("user")
            .build();
        return userRepository.save(newUser);
    }

    /**
     * Microservicio 2: LOGIN
     */
    public LoginResponse login(@Valid LoginRequest request) {
        // ... (código existente sin cambios)
        String email = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return new LoginResponse(false, "", "Credenciales inválidas", null);
        }
        User user = userOpt.get();
        if (passwordEncoder.matches(request.getPassword().trim(), user.getPassHash())) {
            UserProfileResponse userDto = new UserProfileResponse(user);
            userDto.setPassHash(null);
            return new LoginResponse(
                true, 
                "fake-jwt-token-" + email,
                "Login exitoso",
                userDto
            );
        }
        return new LoginResponse(false, "", "Credenciales inválidas", null);
    }

    /**
     * Microservicio 3: GET USER
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    // --- AÑADIDO: (Admin) Listar todos los usuarios ---
    /**
     * Obtiene todos los usuarios de la base de datos.
     * @return Lista de entidades User.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // --- AÑADIDO: (Admin) Eliminar un usuario ---
    /**
     * Elimina un usuario por su email.
     * @return true si se eliminó, false si no se encontró.
     */
    public boolean deleteUser(String email) {
        if (userRepository.existsById(email)) {
            userRepository.deleteById(email);
            return true;
        }
        return false;
    }

    // --- AÑADIDO: (Admin) Actualizar rol de usuario ---
    /**
     * Actualiza el rol de un usuario.
     * @return El usuario actualizado, o Optional.empty() si no se encontró.
     */
    public Optional<User> updateUserRole(String email, @Valid AdminRoleUpdateRequest request) {
        return userRepository.findByEmail(email)
            .map(user -> {
                user.setRol(request.getRol().trim().toLowerCase());
                return userRepository.save(user);
            });
    }

    // --- AÑADIDO: (Perfil) Actualizar perfil del usuario ---
    /**
     * Actualiza los detalles del perfil (nombre, fono, bio, avatar) de un usuario.
     * @return El perfil actualizado, o Optional.empty() si no se encontró.
     */
    public Optional<UserProfileResponse> updateProfile(String email, @Valid ProfileUpdateRequest request) {
        return userRepository.findByEmail(email)
            .map(user -> {
                user.setNombre(request.getNombre().trim());
                user.setFono(request.getFono() != null ? request.getFono().trim() : null);
                user.setBio(request.getBio() != null ? request.getBio().trim() : null);
                user.setAvatarUri(request.getAvatarUri() != null ? request.getAvatarUri().trim() : null);
                User updatedUser = userRepository.save(user);
                return new UserProfileResponse(updatedUser); // Devuelve el DTO
            });
    }

    // --- AÑADIDO: (Checkout) Actualizar suscripción ---
    /**
     * Actualiza los datos de suscripción de un usuario (usado por checkout-service).
     * @return El perfil actualizado, o Optional.empty() si no se encontró.
     */
    public Optional<UserProfileResponse> updateSubscription(String email, @Valid SubscriptionUpdateRequest request) {
        return userRepository.findByEmail(email)
            .map(user -> {
                user.setPlanEndMillis(request.getPlanEndMillis());
                user.setSedeId(request.getSedeId());
                user.setSedeName(request.getSedeName());
                user.setSedeLat(request.getSedeLat());
                user.setSedeLng(request.getSedeLng());
                User updatedUser = userRepository.save(user);
                return new UserProfileResponse(updatedUser); // Devuelve el DTO
            });
    }
}