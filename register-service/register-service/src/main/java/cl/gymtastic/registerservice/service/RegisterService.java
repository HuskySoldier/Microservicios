package cl.gymtastic.registerservice.service;

import cl.gymtastic.registerservice.client.UserClient;
import cl.gymtastic.registerservice.dto.RegisterRequest;
import cl.gymtastic.registerservice.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserClient userClient;

    public UserResponse register(RegisterRequest request) {

        // Aquí podrías validar reglas adicionales
        return userClient.createUser(request);
    }
}
