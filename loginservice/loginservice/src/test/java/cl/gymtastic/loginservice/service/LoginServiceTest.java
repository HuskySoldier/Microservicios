package cl.gymtastic.loginservice.service;

import cl.gymtastic.loginservice.client.UserClient;
import cl.gymtastic.loginservice.dto.LoginRequest;
import cl.gymtastic.loginservice.dto.LoginResponse;
import cl.gymtastic.loginservice.dto.UserProfileResponse;
import feign.FeignException;
import feign.Request; // <-- Importación necesaria
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections; // <-- AÑADIDO: Para Collections.emptyMap()
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private UserClient userClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    private final String TEST_EMAIL = "test@gymtastic.cl";
    private final String RAW_PASSWORD = "test1234";
    private final String HASHED_PASSWORD = "hashed_password_mock";

    @Test
    void login_Success_ReturnsTokenAndProfile() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(RAW_PASSWORD);

        UserProfileResponse userProfile = new UserProfileResponse();
        userProfile.setEmail(TEST_EMAIL);
        userProfile.setPassHash(HASHED_PASSWORD);

        // Mock: Simula que UserClient encuentra el usuario
        when(userClient.buscarPorEmail(TEST_EMAIL)).thenReturn(ResponseEntity.ok(userProfile));
        // Mock: Simula que la contraseña coincide
        when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(true);

        // Act
        LoginResponse response = loginService.login(request);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Login exitoso", response.getMessage());
        assertNotNull(response.getToken());
        assertNotNull(response.getUser());
        // Verifica que el hash se limpie antes de ser devuelto
        assertNull(response.getUser().getPassHash()); 
    }

    @Test
    void login_Failure_UserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@mail.cl");
        request.setPassword(RAW_PASSWORD);

        // --- FIX: Crear un objeto Request mock para evitar el NullPointerException ---
        Request mockRequest = Request.create(
            Request.HttpMethod.GET, 
            "http://mock-url", 
            Collections.emptyMap(), // Headers (Map<String, Collection<String>>)
            Request.Body.create(new byte[0]), // Body
            null // Headers nulos
        );

        // Mock: Simula que UserClient lanza FeignException.NotFound (HTTP 404)
        when(userClient.buscarPorEmail("unknown@mail.cl")).thenThrow(
            // Pasar el objeto mockRequest en lugar de null
            new FeignException.NotFound("404 Not Found", mockRequest, null, null) 
        );

        // Act
        LoginResponse response = loginService.login(request);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Credenciales inválidas (Usuario no existe)", response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    void login_Failure_PasswordIncorrect() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword("incorrecto");

        UserProfileResponse userProfile = new UserProfileResponse();
        userProfile.setPassHash(HASHED_PASSWORD);

        // Mock: Simula que encuentra el usuario pero la clave NO coincide
        when(userClient.buscarPorEmail(TEST_EMAIL)).thenReturn(ResponseEntity.ok(userProfile));
        when(passwordEncoder.matches("incorrecto", HASHED_PASSWORD)).thenReturn(false);

        // Act
        LoginResponse response = loginService.login(request);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Credenciales inválidas (Contraseña incorrecta)", response.getMessage());
    }
}