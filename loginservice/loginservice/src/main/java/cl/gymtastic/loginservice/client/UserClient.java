package cl.gymtastic.loginservice.client;

import cl.gymtastic.loginservice.dto.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// name = nombre lógico
// url = URL física (tomada de application.properties)
@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {

    /**
     * Llama al endpoint GET /users/by-email de user-service
     * (Este endpoint debe existir en user-service y devolver UserProfileResponse)
     */
    @GetMapping("/users/by-email")
    ResponseEntity<UserProfileResponse> buscarPorEmail(@RequestParam String email);
}