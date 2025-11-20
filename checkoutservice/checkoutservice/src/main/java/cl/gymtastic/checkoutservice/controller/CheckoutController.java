package cl.gymtastic.checkoutservice.controller;

import cl.gymtastic.checkoutservice.dto.CheckoutRequest;
import cl.gymtastic.checkoutservice.dto.OrderDto;
import cl.gymtastic.checkoutservice.service.CheckoutException;
import cl.gymtastic.checkoutservice.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
// ELIMINADO: @RequestMapping("/checkout") para poder usar múltiples rutas base ("checkout" y "orders")
@CrossOrigin
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    // 1. Ruta para el PAGO (Coincide con @POST("$BASE_IP:8086/checkout"))
    @Operation(summary = "Procesar un pago (Orquestador)")
    @PostMapping("/checkout") 
    public ResponseEntity<Map<String, Object>> processCheckout(@RequestBody CheckoutRequest request) {
        try {
            Map<String, Object> response = checkoutService.processCheckout(request);
            return ResponseEntity.ok(response);
        } catch (CheckoutException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", e.getMessage(), "planActivated", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage(), "planActivated", false));
        }
    }

    // 2. Ruta para el HISTORIAL (Coincide con @GET("$BASE_IP:8086/orders/{email}"))
    @Operation(summary = "Obtener historial de compras")
    @GetMapping("/orders/{email}") 
    public ResponseEntity<List<OrderDto>> getOrderHistory(@PathVariable String email) {
        // Asegúrate de que el método en tu servicio se llame 'getOrderHistory'
        return ResponseEntity.ok(checkoutService.getOrderHistory(email));
    }
}