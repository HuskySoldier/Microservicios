package cl.gymtastic.loginservice.controller;

import cl.gymtastic.loginservice.dto.LoginRequest;
import cl.gymtastic.loginservice.dto.LoginResponse;
import cl.gymtastic.loginservice.dto.ResetPasswordRequest; // DTO correcto
import cl.gymtastic.loginservice.service.LoginService;

// --- IMPORTS DE SWAGGER (Documentación) ---
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
// ELIMINAR ESTE IMPORT: import io.swagger.v3.oas.annotations.parameters.RequestBody;

// --- IMPORTS DE SPRING (Funcionalidad) ---
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Incluye @RequestBody, @PostMapping, etc.

@RestController
@RequestMapping("/login")
@Tag(name = "Login Service", description = "Endpoint para autenticar usuarios")
@CrossOrigin
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Operation(summary = "Paso 1: Solicitar recuperación de contraseña")
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestParam String email) {
        loginService.requestReset(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Paso 2: Confirmar nueva contraseña con token")
    @PostMapping("/confirm-reset")
    // @RequestBody aquí debe ser de org.springframework.web.bind.annotation
    public ResponseEntity<?> confirmReset(@RequestBody ResetPasswordRequest request) {
        boolean success = loginService.confirmReset(request);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Código inválido o error al procesar.");
        }
    }

    @Operation(summary = "Autenticar un usuario")
    @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
}