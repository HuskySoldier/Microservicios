package cl.gymtastic.registerservice.service;

import cl.gymtastic.registerservice.client.UserClient;
import cl.gymtastic.registerservice.dto.RegisterRequest;
// UserResponse ya no se usa aquí
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; // Importar
import org.springframework.stereotype.Service;
import java.util.Map; // Importar

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserClient userClient;

    // CORREGIDO: Tipo de retorno
    public ResponseEntity<Map<String, String>> register(RegisterRequest request) {
        
        // Simplemente llamamos al cliente y devolvemos su respuesta
        return userClient.createUser(request);
    }
}