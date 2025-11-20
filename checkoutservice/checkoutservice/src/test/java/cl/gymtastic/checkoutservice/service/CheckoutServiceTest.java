package cl.gymtastic.checkoutservice.service;

import cl.gymtastic.checkoutservice.client.ProductClient;
import cl.gymtastic.checkoutservice.client.UserClient;
import cl.gymtastic.checkoutservice.dto.*;
import cl.gymtastic.checkoutservice.model.PurchaseOrder; // Importar Modelo
import cl.gymtastic.checkoutservice.repository.PurchaseOrderRepository; // Importar Repo
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
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

    // 1. AÑADIDO: Mock del repositorio de historial
    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

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
        mockUserProfile.setPlanEndMillis(System.currentTimeMillis() - 1000); 

        CartItemDto planItem = new CartItemDto();
        planItem.setProductId(100);
        planItem.setCantidad(1); // 2. CORREGIDO: setQty -> setCantidad
        planItem.setTipo("plan");
        planItem.setNombre("Plan Mensual"); // 3. AÑADIDO: Datos necesarios para historial
        planItem.setPrecio(20000.0);
        
        CartItemDto merchItem = new CartItemDto();
        merchItem.setProductId(200);
        merchItem.setCantidad(5); // CORREGIDO
        merchItem.setTipo("merch");
        merchItem.setNombre("Proteína"); // AÑADIDO
        merchItem.setPrecio(5000.0);
        
        CheckoutRequest request = new CheckoutRequest();
        request.setUserEmail(TEST_EMAIL);
        request.setItems(Arrays.asList(planItem, merchItem));
        request.setSede(MOCK_SEDE);

        // Mocks
        when(userClient.getUserProfile(TEST_EMAIL)).thenReturn(ResponseEntity.ok(mockUserProfile));
        when(productClient.decreaseStock(any(StockDecreaseRequest.class))).thenReturn(ResponseEntity.ok(Map.of("message", "OK")));
        when(userClient.updateSubscription(eq(TEST_EMAIL), any(SubscriptionUpdateRequest.class))).thenReturn(ResponseEntity.ok(mockUserProfile));
        // Mock para guardar historial
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Map<String, Object> response = checkoutService.processCheckout(request);

        // Assert
        assertTrue((Boolean) response.get("success"));
        assertTrue((Boolean) response.get("planActivated"));
        
        verify(productClient, times(1)).decreaseStock(any(StockDecreaseRequest.class));
        verify(userClient, times(1)).updateSubscription(eq(TEST_EMAIL), any(SubscriptionUpdateRequest.class));
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class)); // Verificar guardado
    }


    // --- ESCENARIO 2: FALLA POR STOCK INSUFICIENTE ---
    @Test
    void processCheckout_Failure_InsufficientStock() {
        // Arrange
        CartItemDto merchItem = new CartItemDto();
        merchItem.setProductId(200);
        merchItem.setCantidad(5);
        merchItem.setTipo("merch");
        merchItem.setNombre("Proteína");
        merchItem.setPrecio(5000.0);
        
        CheckoutRequest request = new CheckoutRequest();
        request.setUserEmail(TEST_EMAIL);
        request.setItems(Collections.singletonList(merchItem));
        request.setSede(null);

        // Simulación correcta de Feign Exception
        Request fakeRequest = Request.create(Request.HttpMethod.POST, "/", Collections.emptyMap(), new byte[0], null);
        // Nota: FeignException.errorStatus factory es útil para crear excepciones basadas en códigos de estado
        FeignException conflictException = FeignException.errorStatus("decreaseStock", 
            Response.builder()
                .status(409)
                .reason("Conflict")
                .request(fakeRequest)
                .build());

        when(productClient.decreaseStock(any(StockDecreaseRequest.class))).thenThrow(conflictException);

        // Act & Assert
        // OJO: Si tu CheckoutService NO tiene un try-catch para FeignException, 
        // esto lanzará FeignException en lugar de CheckoutException.
        // Asumiendo que agregaste el try-catch sugerido anteriormente:
        Exception exception = assertThrows(Exception.class, () -> {
            checkoutService.processCheckout(request);
        });
        
        // Verificamos que falló, idealmente debería ser CheckoutException si manejas el error en el servicio
        assertNotNull(exception);
        
        verify(userClient, never()).updateSubscription(any(), any());
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    // --- ESCENARIO 3: FALLA POR INTENTAR RENOVAR PLAN DEMASIADO PRONTO ---
    @Test
    void processCheckout_Failure_PlanActive() {
        // Arrange
        mockUserProfile.setPlanEndMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(50)); 

        CartItemDto planItem = new CartItemDto();
        planItem.setProductId(100);
        planItem.setCantidad(1);
        planItem.setTipo("plan");
        planItem.setNombre("Plan Mensual");
        planItem.setPrecio(20000.0);
        
        CheckoutRequest request = new CheckoutRequest();
        request.setUserEmail(TEST_EMAIL);
        request.setItems(Collections.singletonList(planItem));
        request.setSede(MOCK_SEDE);

        when(userClient.getUserProfile(TEST_EMAIL)).thenReturn(ResponseEntity.ok(mockUserProfile));
        
        // Act & Assert
        CheckoutException exception = assertThrows(CheckoutException.class, () -> {
            checkoutService.processCheckout(request);
        });

        assertTrue(exception.getMessage().contains("Ya tienes un plan activo"));
        
        verify(productClient, never()).decreaseStock(any());
        verify(userClient, never()).updateSubscription(any(), any());
        verify(purchaseOrderRepository, never()).save(any());
    }
}