package cl.gymtastic.loginservice.service;

import cl.gymtastic.loginservice.client.UserClient;
import cl.gymtastic.loginservice.dto.LoginRequest;
import cl.gymtastic.loginservice.dto.UserProfileResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testLoginFlow_Success() throws Exception {

        // 🔹 Preparar datos del usuario simulado
        String email = "test@gym.cl";
        String rawPassword = "123456";
        String hashedPassword = passwordEncoder.encode(rawPassword);

        UserProfileResponse mockProfile = new UserProfileResponse();
        mockProfile.setEmail(email);
        mockProfile.setPassHash(hashedPassword);
        mockProfile.setRol("User");

        // 🔹 Simular respuesta desde User-Service
        Mockito.when(userClient.buscarPorEmail(email))
                .thenReturn(ResponseEntity.ok(mockProfile));

        // 🔹 Crear login request JSON
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(rawPassword);

        // 🔹 Ejecutar /login y verificar resultados
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequest)))
                // 4. Verificar Resultados
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").exists()) // Verifica que se generó JWT
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").value(org.hamcrest.Matchers.not("fake-jwt-token"))); // No debe ser el fake
    }
}
