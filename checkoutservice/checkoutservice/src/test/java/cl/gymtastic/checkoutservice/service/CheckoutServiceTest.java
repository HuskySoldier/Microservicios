package cl.gymtastic.checkoutservice.service;

import cl.gymtastic.checkoutservice.client.ProductClient;
import cl.gymtastic.checkoutservice.dto.StockDecreaseRequest;
import cl.gymtastic.checkoutservice.client.UserClient;
import cl.gymtastic.checkoutservice.dto.*;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckoutServiceTest {

    @Mock
    private UserClient userClient;
    
    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CheckoutService checkoutService;

    private final String TEST_EMAIL = "user@test.cl";
    private final SedeDto MOCK_SEDE = new SedeDto();
    private UserProfileResponse mockUserProfile;

    @BeforeEach
    void setUp() {
        MOCK_SEDE.setId(1);
        MOCK_SEDE.setNombre("Sede Central");
        MOCK_SEDE.setLat(-33.4);
        MOCK_SEDE.setLng(-70.6);
        
        mockUserProfile = new UserProfileResponse();
        mockUserProfile.setEmail(TEST_EMAIL);
    }

    // --- ESCENARIO 1: COMPRA DE PLAN Y MERCH (ÉXITO) ---
    @Test
    void processCheckout_Success_PlanAndMerch() {
        // Arrange
        // Mock de un usuario que NO tiene plan activo (o está expirado)
        mockUserProfile.setPlanEndMillis(System.currentTimeMillis() - 1000); 

        CartItemDto planItem = new CartItemDto();
        planItem.setProductId(100);
        planItem.setQty(1);
        planItem.setTipo("plan");
        
        CartItemDto merchItem = new CartItemDto();
        merchItem.setProductId(200);
        merchItem.setQty(5);
        merchItem.setTipo("merch");
        
        CheckoutRequest request = new CheckoutRequest();
        request.setUserEmail(TEST_EMAIL);
        request.setItems(Arrays.asList(planItem, merchItem));
        request.setSede(MOCK_SEDE);

        // Mock: 1. Estado del usuario
        when(userClient.getUserProfile(TEST_EMAIL)).thenReturn(ResponseEntity.ok(mockUserProfile));
        // Mock: 2. Descuento de stock OK
        when(productClient.decreaseStock(any(StockDecreaseRequest.class))).thenReturn(ResponseEntity.ok(Map.of("message", "OK")));
        // Mock: 3. Actualización de plan OK
        when(userClient.updateSubscription(eq(TEST_EMAIL), any(SubscriptionUpdateRequest.class))).thenReturn(ResponseEntity.ok(mockUserProfile));

        // Act
        Map<String, Object> response = checkoutService.processCheckout(request);

        // Assert
        assertTrue((Boolean) response.get("success"));
        assertTrue((Boolean) response.get("planActivated"));
        
        // Verifica que se llamó a los clientes correctos
        verify(productClient, times(1)).decreaseStock(any(StockDecreaseRequest.class));
        verify(userClient, times(1)).updateSubscription(eq(TEST_EMAIL), any(SubscriptionUpdateRequest.class));
    }


    // --- ESCENARIO 2: FALLA POR STOCK INSUFICIENTE ---
    @Test
    void processCheckout_Failure_InsufficientStock() {
        // Arrange: Solo merch, sin planes
        CartItemDto merchItem = new CartItemDto();
        merchItem.setProductId(200);
        merchItem.setQty(5);
        merchItem.setTipo("merch");
        
        CheckoutRequest request = new CheckoutRequest();
        request.setUserEmail(TEST_EMAIL);
        request.setItems(Collections.singletonList(merchItem));
        request.setSede(null);

        // Mock: Descuento de stock falla con FeignException (simulando HTTP 409)
        // Construimos un Response simulado y usamos FeignException.errorStatus(...) para crear la excepción
        feign.Request fakeRequest = feign.Request.create(feign.Request.HttpMethod.POST, "/", Collections.emptyMap(), new byte[0], null);
        feign.Response fakeResponse = feign.Response.builder()
            .status(409)
            .reason("Conflict")
            .request(fakeRequest)
            .headers(Collections.emptyMap())
            .build();
        when(productClient.decreaseStock(any(StockDecreaseRequest.class))).thenThrow(FeignException.errorStatus("decreaseStock", fakeResponse));

        // Act & Assert
        CheckoutException exception = assertThrows(CheckoutException.class, () -> {
            checkoutService.processCheckout(request);
        });
        
        assertTrue(exception.getMessage().contains("Error al descontar stock"));
        
        // Verifica que la actualización del plan NUNCA fue llamada
        verify(userClient, never()).updateSubscription(any(), any());
    }
    
    // --- ESCENARIO 3: FALLA POR INTENTAR RENOVAR PLAN DEMASIADO PRONTO ---
    @Test
    void processCheckout_Failure_PlanActive() {
        // Arrange
        // Mock de un usuario que tiene plan activo (expira en 50 días)
        mockUserProfile.setPlanEndMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(50)); 

        CartItemDto planItem = new CartItemDto();
        planItem.setProductId(100);
        planItem.setQty(1);
        planItem.setTipo("plan");
        
        CheckoutRequest request = new CheckoutRequest();
        request.setUserEmail(TEST_EMAIL);
        request.setItems(Collections.singletonList(planItem));
        request.setSede(MOCK_SEDE);

        // Mock: 1. Estado del usuario
        when(userClient.getUserProfile(TEST_EMAIL)).thenReturn(ResponseEntity.ok(mockUserProfile));
        
        // Act & Assert
        CheckoutException exception = assertThrows(CheckoutException.class, () -> {
            checkoutService.processCheckout(request);
        });

        assertTrue(exception.getMessage().contains("Ya tienes un plan activo"));
        
        // Verifica que el descuento de stock y la actualización del plan NUNCA fueron llamados
        verify(productClient, never()).decreaseStock(any());
        verify(userClient, never()).updateSubscription(any(), any());
    }
}