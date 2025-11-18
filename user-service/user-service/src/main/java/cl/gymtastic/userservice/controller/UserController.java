package cl.gymtastic.userservice.controller;

// --- Importar los nuevos DTOs y List/stream ---
import cl.gymtastic.userservice.dto.*;
import cl.gymtastic.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // <-- Importar List
import java.util.Map;
import java.util.stream.Collectors; // <-- Importar Collectors

@RestController
@CrossOrigin
@Tag(name = "User & Auth Service", description = "Endpoints para Registro, Login y Perfiles")
public class UserController {

    @Autowired
    private UserService userService;

    // --- (Existente) MICROSERVICIO 1: REGISTER ---
    @Operation(summary = "Registrar un nuevo usuario")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // ... (código existente sin cambios)
        try {
            userService.register(request);
            return ResponseEntity.status(201).body(Map.of("message", "Usuario creado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --- (Existente) MICROSERVICIO 2: LOGIN ---
    @Operation(summary = "Autenticar un usuario")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // ... (código existente sin cambios)
        LoginResponse response = userService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    // --- (Existente) MICROSERVICIO 3: GET USER PROFILE ---
    @Operation(summary = "Obtener perfil de usuario por Email (PathVariable)")
    @GetMapping("/users/{email}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(new UserProfileResponse(user))) 
                .orElse(ResponseEntity.notFound().build());
    }

    // --- ¡FIX IMPORTANTE! Endpoint para loginservice ---
    // loginservice busca este endpoint:
    @Operation(summary = "Obtener perfil de usuario por Email (RequestParam)")
    @GetMapping("/users/by-email")
    public ResponseEntity<UserProfileResponse> getUserProfileByRequestParam(@RequestParam String email) {
        return userService.getUserByEmail(email)
                // ¡IMPORTANTE! El loginservice espera el hash de la contraseña.
                // Usamos la entidad User directamente aquí, no el DTO
                .map(user -> ResponseEntity.ok(new UserProfileResponse(user))) 
                .orElse(ResponseEntity.notFound().build());
    }
    
    // --- AÑADIDO: (Admin) Listar todos los usuarios ---
    @Operation(summary = "Listar todos los usuarios (Admin)")
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> userProfiles = userService.getAllUsers()
                .stream()
                .map(UserProfileResponse::new) // Mapea a DTO para no exponer el hash
                .collect(Collectors.toList());
        return ResponseEntity.ok(userProfiles);
    }

    // --- AÑADIDO: (Admin) Eliminar un usuario ---
    @Operation(summary = "Eliminar un usuario (Admin)")
    @DeleteMapping("/users/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        if (userService.deleteUser(email)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // --- AÑADIDO: (Admin) Actualizar rol de un usuario ---
    @Operation(summary = "Actualizar el rol de un usuario (Admin)")
    @PutMapping("/users/{email}/role")
    public ResponseEntity<UserProfileResponse> updateUserRole(@PathVariable String email, @Valid @RequestBody AdminRoleUpdateRequest request) {
        return userService.updateUserRole(email, request)
                .map(user -> ResponseEntity.ok(new UserProfileResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- AÑADIDO: (Perfil) Actualizar perfil del propio usuario ---
    @Operation(summary = "Actualizar el perfil de un usuario (nombre, fono, bio, avatar)")
    @PutMapping("/users/{email}/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@PathVariable String email, @Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(email, request)
                .map(profile -> ResponseEntity.ok(profile))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // --- AÑADIDO: (Checkout) Actualizar suscripción ---
    @Operation(summary = "Actualizar la suscripción de un usuario (Checkout Service)")
    @PutMapping("/users/{email}/subscription")
    public ResponseEntity<UserProfileResponse> updateSubscription(@PathVariable String email, @Valid @RequestBody SubscriptionUpdateRequest request) {
        return userService.updateSubscription(email, request)
                .map(profile -> ResponseEntity.ok(profile))
                .orElse(ResponseEntity.notFound().build());
    }
    @Operation(summary = "Solicitar reset de contraseña (Interno)")
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestParam String email) {
        userService.requestPasswordReset(email);
        return ResponseEntity.ok(Map.of("message", "Si el correo existe, se ha enviado un código."));
    }

    @Operation(summary = "Confirmar reset de contraseña (Interno)")
    @PostMapping("/confirm-reset")
    public ResponseEntity<?> confirmReset(@RequestBody ResetPasswordRequest request) {
        boolean success = userService.confirmPasswordReset(request);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Token inválido o expirado."));
        }
    }
}