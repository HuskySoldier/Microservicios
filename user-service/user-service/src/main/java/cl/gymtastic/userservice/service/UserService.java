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

import java.util.List; 
import java.util.Optional;
import java.util.Random;
import java.util.Objects; // <-- AÑADIDO: Para chequeos de nulidad en el servicio

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
    @SuppressWarnings("null")
    public User register(@Valid RegisterRequest request) {
        // CORRECCIÓN: Usamos Objects.requireNonNull() para asegurar que el valor 
        // existe antes de llamar a trim(), satisfaciendo el análisis estático.
        String normalizedEmail = Objects.requireNonNull(request.getEmail()).trim().toLowerCase();
        
        if (userRepository.existsById(normalizedEmail)) {
            throw new RuntimeException("El email ya existe");
        }
        
        // CORRECCIÓN: Aplicamos lo mismo al password y nombre.
        String passHash = passwordEncoder.encode(Objects.requireNonNull(request.getPassword()).trim()); 
        User newUser = User.builder()
            .email(normalizedEmail)
            .passHash(passHash)
            .nombre(Objects.requireNonNull(request.getNombre()).trim())
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
        
        // CORRECCIÓN: Usamos .orElseThrow() o la aserción 'get()' después de un 
        // .isPresent/isEmpty. Como ya comprobamos .isEmpty, podemos usar .get().
        // La advertencia original era aquí (Línea 42):
        User user = userOpt.get(); // userOpt.get() es seguro porque se comprobó userOpt.isEmpty()
        
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
    @SuppressWarnings("null")
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
                user.setRol(Objects.requireNonNull(request.getRol()).trim().toLowerCase());
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
                // CORRECCIÓN: Aplicamos Objects.requireNonNull() o manejamos el null con ternario.
                // Corrección para la advertencia en Línea 91 y 92:
                user.setNombre(Objects.requireNonNull(request.getNombre()).trim());
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

    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email.trim().toLowerCase()).ifPresent(user -> {
            // Generar código de 6 dígitos
            String token = String.format("%06d", new Random().nextInt(999999));
            
            user.setResetToken(token);
            userRepository.save(user);

            // --- SIMULACIÓN: IMPRIMIR EN CONSOLA ---
            System.out.println("=============================================");
            System.out.println(" [DEV] TOKEN DE RECUPERACIÓN PARA: " + email);
            System.out.println(" [DEV] CÓDIGO: " + token);
            System.out.println("=============================================");
        });
        // Por seguridad, no lanzamos error si el email no existe
    }

    // 2. Confirmar y Cambiar Contraseña
    public boolean confirmPasswordReset(ResetPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().trim().toLowerCase());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Validar que el token exista y coincida
            if (user.getResetToken() != null && user.getResetToken().equals(request.getToken().trim())) {
                
                // Encriptar la nueva contraseña
                user.setPassHash(passwordEncoder.encode(request.getNewPassword().trim()));
                
                // Borrar el token para que no se use de nuevo
                user.setResetToken(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}