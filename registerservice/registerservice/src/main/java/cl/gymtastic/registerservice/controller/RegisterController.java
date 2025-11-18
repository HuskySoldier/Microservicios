package cl.gymtastic.registerservice.controller;

import cl.gymtastic.registerservice.dto.RegisterRequest;
// UserResponse ya no se usa aquí
import cl.gymtastic.registerservice.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; // Importar
import org.springframework.web.bind.annotation.*;
import java.util.Map; // Importar

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    // CORREGIDO: Tipo de retorno
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        return registerService.register(request);
    }
}