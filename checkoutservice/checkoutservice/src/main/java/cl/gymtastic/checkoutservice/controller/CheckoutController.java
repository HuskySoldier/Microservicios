package cl.gymtastic.checkoutservice.controller;

import cl.gymtastic.checkoutservice.dto.CheckoutRequest;
import cl.gymtastic.checkoutservice.service.CheckoutException;
import cl.gymtastic.checkoutservice.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/checkout")
@CrossOrigin
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @Operation(summary = "Procesar un pago (Orquestador)")
    @PostMapping
    public ResponseEntity<Map<String, Object>> processCheckout(@RequestBody CheckoutRequest request) {
        try {
            Map<String, Object> response = checkoutService.processCheckout(request);
            return ResponseEntity.ok(response);
        } catch (CheckoutException e) {
            // 409 Conflict para errores de lógica de negocio (stock, plan activo)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", e.getMessage(), "planActivated", false));
        } catch (Exception e) {
            // 500 para errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage(), "planActivated", false));
        }
    }
}