package cl.gymtastic.registerservice.client;

import cl.gymtastic.registerservice.dto.RegisterRequest;
import cl.gymtastic.registerservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserClient {

    @PostMapping("/users")
    UserResponse createUser(RegisterRequest request);
}
