package cl.gymtastic.user_service.service;

import cl.gymtastic.userservice.dto.LoginRequest;
import cl.gymtastic.userservice.dto.LoginResponse;
import cl.gymtastic.userservice.dto.ProfileUpdateRequest;
import cl.gymtastic.userservice.dto.RegisterRequest;
import cl.gymtastic.userservice.dto.UserProfileResponse;
import cl.gymtastic.userservice.model.User;
import cl.gymtastic.userservice.repository.UserRepository;
import cl.gymtastic.userservice.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Objeto base que simula un usuario de la base de datos
        testUser = User.builder()
                .email("test@gymtastic.cl")
                .passHash("hashed_password_mocked")
                .nombre("Usuario Test")
                .rol("user")
                .build();
    }

    // --- PRUEBAS DE REGISTRO ---

    @Test
    @SuppressWarnings("null") // <-- CORRECCIÓN: Suprime advertencias de nulidad (Línea 63)
    void register_Success() {
        // Prepara la solicitud
        RegisterRequest request = new RegisterRequest();
        request.setEmail("nuevo@gymtastic.cl");
        request.setPassword("newpass123");
        request.setNombre("Nuevo Usuario");

        // Mock: Simula que el email no existe y codifica la clave
        when(userRepository.existsById("nuevo@gymtastic.cl")).thenReturn(false);
        when(passwordEncoder.encode("newpass123")).thenReturn("new_hashed_pass");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Ejecuta
        User result = userService.register(request);

        // Verifica
        assertNotNull(result);
        // Asegura que se llamó a save()
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @SuppressWarnings("null") // <-- CORRECCIÓN: Suprime advertencias de nulidad (Línea 71)
    void register_Failure_EmailExists() {
        // Prepara la solicitud
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existente@gymtastic.cl");

        // Mock: Simula que el email ya existe
        when(userRepository.existsById("existente@gymtastic.cl")).thenReturn(true);

        // Ejecuta y verifica que se lance la RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(request);
        });

        assertEquals("El email ya existe", exception.getMessage());
        // Asegura que NUNCA se llamó a save()
        verify(userRepository, never()).save(any(User.class));
    }

    // --- PRUEBAS DE LOGIN ---

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gymtastic.cl");
        request.setPassword("test1234");
        
        // Mock: Encuentra el usuario y la clave coincide
        when(userRepository.findByEmail("test@gymtastic.cl")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("test1234", "hashed_password_mocked")).thenReturn(true);

        LoginResponse response = userService.login(request);

        assertTrue(response.isSuccess());
        assertEquals("Login exitoso", response.getMessage());
        assertNotNull(response.getUser());
        // El DTO de respuesta no expone passHash; verificamos que el usuario esté presente.
    }

    @Test
    void login_Failure_PasswordIncorrect() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gymtastic.cl");
        request.setPassword("wrongpass");

        // Mock: Encuentra el usuario, pero la clave NO coincide
        when(userRepository.findByEmail("test@gymtastic.cl")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpass", "hashed_password_mocked")).thenReturn(false);

        LoginResponse response = userService.login(request);

        assertFalse(response.isSuccess());
        assertEquals("Credenciales inválidas", response.getMessage());
    }

    // --- PRUEBAS DE ACTUALIZACIÓN DE PERFIL ---

    @Test
    @SuppressWarnings("null") // <-- CORRECCIÓN: Suprime advertencias de nulidad (Líneas 140, 150)
    void updateProfile_Success() {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setNombre("Nuevo Nombre");
        request.setFono("+56912345678");

        // Mock: Encuentra el usuario
        when(userRepository.findByEmail("test@gymtastic.cl")).thenReturn(Optional.of(testUser));
        // Mock: Simula que save() devuelve el objeto que se le pasó (el actualizado)
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<UserProfileResponse> responseOpt = userService.updateProfile("test@gymtastic.cl", request);

        assertTrue(responseOpt.isPresent());
        UserProfileResponse response = responseOpt.get();
        // Verifica que los campos del DTO de respuesta son los nuevos
        assertEquals("Nuevo Nombre", response.getNombre());
        assertEquals("+56912345678", response.getFono());
        
        verify(userRepository, times(1)).save(testUser);
    }
}