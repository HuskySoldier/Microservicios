package cl.gymtastic.checkoutservice.service; // <--- 1. PAQUETE CORREGIDO (Era product_service)

// --- 2. IMPORTACIONES CORREGIDAS (Apuntan a checkoutservice) ---
import cl.gymtastic.checkoutservice.client.ProductClient;
import cl.gymtastic.checkoutservice.client.UserClient;
import cl.gymtastic.checkoutservice.dto.CartItemDto;
import cl.gymtastic.checkoutservice.dto.CheckoutRequest;
import cl.gymtastic.checkoutservice.dto.UserProfileResponse;
// --------------------------------------------------------------

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureMockMvc
public class CheckoutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Mockeamos los clientes Feign
    @MockBean
    private UserClient userClient;

    @MockBean
    private ProductClient productClient;

    @Test
    void testProcessCheckout_Flow() throws Exception {
        // 1. DATOS DE PRUEBA
        CheckoutRequest request = new CheckoutRequest();
        request.setUserEmail("cliente@test.cl");
        
        CartItemDto item = new CartItemDto();
        item.setProductId(1);
        item.setCantidad(2);
        item.setTipo("merch"); // Asegúrate que tu DTO tenga este campo
        item.setPrecio(1000.0);
        item.setNombre("Mancuerna");
        
        request.setItems(Collections.singletonList(item));

        // 2. SIMULAR RESPUESTAS DE MICROSERVICIOS EXTERNOS
        
        // Simula que product-service descuenta stock correctamente
        Mockito.when(productClient.decreaseStock(any()))
               .thenReturn(ResponseEntity.ok(Map.of("message", "Stock OK")));
               
        // Simula que user-service encuentra al usuario
        UserProfileResponse mockProfile = new UserProfileResponse();
        mockProfile.setEmail("cliente@test.cl");
        // Asegúrate que UserProfileResponse tenga los campos necesarios (ej. planEndMillis si aplica)
        
        Mockito.when(userClient.getUserProfile("cliente@test.cl"))
               .thenReturn(ResponseEntity.ok(mockProfile));

        // 3. EJECUTAR PETICIÓN HTTP AL ENDPOINT /checkout
        mockMvc.perform(post("/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                
                // 4. VERIFICAR QUE RESPONDE 200 OK Y JSON CORRECTO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Compra exitosa."));
    }
}