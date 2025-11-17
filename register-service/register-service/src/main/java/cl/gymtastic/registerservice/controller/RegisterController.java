package cl.gymtastic.registerservice.controller;

import cl.gymtastic.registerservice.dto.RegisterRequest;
import cl.gymtastic.registerservice.dto.UserResponse;
import cl.gymtastic.registerservice.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return registerService.register(request);
    }
}
