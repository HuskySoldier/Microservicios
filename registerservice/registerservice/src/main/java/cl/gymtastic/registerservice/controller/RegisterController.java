package cl.gymtastic.registerservice.controller;

import cl.gymtastic.registerservice.dto.RegisterRequest;
import cl.gymtastic.registerservice.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Importa CrossOrigin aquí
import java.util.Map;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@CrossOrigin // <--- ¡IMPORTANTE! Agrega esto para permitir la conexión con el Frontend
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        return registerService.register(request);
    }
}