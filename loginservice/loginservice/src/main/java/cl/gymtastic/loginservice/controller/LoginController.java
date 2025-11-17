package cl.gymtastic.loginservice.controller;

import cl.gymtastic.loginservice.dto.LoginRequest;
import cl.gymtastic.loginservice.dto.LoginResponse;
import cl.gymtastic.loginservice.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login") // Ruta base
@Tag(name = "Login Service", description = "Endpoint para autenticar usuarios")
@CrossOrigin // Permite CORS
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Operation(summary = "Autenticar un usuario")
    @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // 401 Unauthorized es el código correcto para credenciales fallidas
            return ResponseEntity.status(401).body(response);
        }
    }
}