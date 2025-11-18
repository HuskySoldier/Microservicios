package cl.gymtastic.registerservice.client;

import cl.gymtastic.registerservice.dto.RegisterRequest;
import java.util.Map; // Importar Map
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity; // Importar ResponseEntity
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {

    @PostMapping("/register") // <-- CORREGIDO: Endpoint es '/register'
    // CORREGIDO: Tipo de retorno
    ResponseEntity<Map<String, String>> createUser(RegisterRequest request); 
}