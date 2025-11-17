package cl.gymtastic.userservice.controller;

import cl.gymtastic.userservice.dto.LoginRequest;
import cl.gymtastic.userservice.dto.LoginResponse;
import cl.gymtastic.userservice.dto.RegisterRequest;
import cl.gymtastic.userservice.dto.UserProfileResponse;
import cl.gymtastic.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // Para el mensaje simple

@RestController
@CrossOrigin // Permite CORS
@Tag(name = "User & Auth Service", description = "Endpoints para Registro, Login y Perfiles")
public class UserController {

    @Autowired
    private UserService userService;

    // --- MICROSERVICIO 1: REGISTER ---
    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Email ya existe o datos inválidos")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.status(201).body(Map.of("message", "Usuario creado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --- MICROSERVICIO 2: LOGIN ---
    @Operation(summary = "Autenticar un usuario")
    @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    // --- MICROSERVICIO 3: GET USER PROFILE ---
    @Operation(summary = "Obtener perfil de usuario por Email")
    @ApiResponse(responseCode = "200", description = "Perfil encontrado", content = @Content(schema = @Schema(implementation = UserProfileResponse.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    @GetMapping("/users/{email}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(new UserProfileResponse(user))) // Mapea Entidad a DTO
                .orElse(ResponseEntity.notFound().build());
    }

    // (Aquí irían los endpoints PUT/POST para actualizar perfil que llamen al UserService)
    // Ej: @PutMapping("/users/{email}/profile") ...
}