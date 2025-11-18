package cl.gymtastic.checkoutservice.client;

import cl.gymtastic.checkoutservice.dto.SubscriptionUpdateRequest;
import cl.gymtastic.checkoutservice.dto.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {

    // Usamos el endpoint que creamos en Tarea B
    @GetMapping("/users/{email}")
    ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable("email") String email);

    // Usamos el endpoint que creamos en Tarea B
    @PutMapping("/users/{email}/subscription")
    ResponseEntity<UserProfileResponse> updateSubscription(
            @PathVariable("email") String email,
            @RequestBody SubscriptionUpdateRequest request
    );
}