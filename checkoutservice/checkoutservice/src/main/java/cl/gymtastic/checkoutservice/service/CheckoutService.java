package cl.gymtastic.checkoutservice.service;

import cl.gymtastic.checkoutservice.client.ProductClient;
import cl.gymtastic.checkoutservice.client.UserClient;
import cl.gymtastic.checkoutservice.dto.*;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CheckoutService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProductClient productClient;

    // Lógica de "canBuy" replicada de PlanesScreen.kt
    private final int THRESHOLD_DAYS = 3; 

    public Map<String, Object> processCheckout(CheckoutRequest request) {
        
        boolean hasPlanInCart = request.getItems().stream().anyMatch(item -> "plan".equals(item.getTipo()));
        List<CartItemDto> merchItems = request.getItems().stream()
                .filter(item -> "merch".equals(item.getTipo()))
                .collect(Collectors.toList());

        // --- 1. Validar Plan (si aplica) ---
        if (hasPlanInCart) {
            // 1a. Validar que se seleccionó una sede
            if (request.getSede() == null) {
                throw new CheckoutException("Selecciona una sede para asociar tu plan.");
            }
            
            // 1b. Validar si el usuario puede comprar un plan
            ResponseEntity<UserProfileResponse> userResponse = userClient.getUserProfile(request.getUserEmail());
            if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
                throw new CheckoutException("Usuario no encontrado.");
            }
            
            UserProfileResponse user = userResponse.getBody();
            if (!canBuyPlan(user.getPlanEndMillis())) {
                long remainingDays = TimeUnit.MILLISECONDS.toDays(user.getPlanEndMillis() - System.currentTimeMillis());
                throw new CheckoutException("Ya tienes un plan activo. Podrás renovar cuando falten " + THRESHOLD_DAYS + " días o menos. Restan: " + remainingDays);
            }
        }

        // --- 2. Descontar Stock de Merch (si aplica) ---
        if (!merchItems.isEmpty()) {
            try {
                ResponseEntity<Map<String, String>> stockResponse = productClient.decreaseStock(new StockDecreaseRequest(merchItems));
                if (!stockResponse.getStatusCode().is2xxSuccessful()) {
                    // Esto captura el 409 Conflict (stock insuficiente)
                    throw new CheckoutException("Stock insuficiente para uno o más productos.");
                }
            } catch (FeignException e) {
                // Captura el 409 (Conflict) u otros errores
                throw new CheckoutException("Error al descontar stock: " + e.contentUTF8());
            }
        }

        // --- 3. Activar Plan (si aplica) ---
        if (hasPlanInCart) {
            SedeDto sede = request.getSede();
            SubscriptionUpdateRequest subRequest = new SubscriptionUpdateRequest();
            
            // Lógica de 30 días de PaymentScreen.kt
            long newPlanEnd = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30);
            
            subRequest.setPlanEndMillis(newPlanEnd);
            subRequest.setSedeId(sede.getId());
            subRequest.setSedeName(sede.getNombre());
            subRequest.setSedeLat(sede.getLat());
            subRequest.setSedeLng(sede.getLng());
            
            // Llamar a user-service para actualizar la suscripción
            userClient.updateSubscription(request.getUserEmail(), subRequest);
        }

        return Map.of(
            "success", true,
            "message", "Compra procesada exitosamente.",
            "planActivated", hasPlanInCart
        );
    }
    
    // Lógica de canBuy de la App
    private boolean canBuyPlan(Long planEndMillis) {
        if (planEndMillis == null) return true; // No tiene plan
        long diff = planEndMillis - System.currentTimeMillis();
        if (diff <= 0) return true; // Plan expiró
        long daysRemaining = TimeUnit.MILLISECONDS.toDays(diff);
        return daysRemaining <= THRESHOLD_DAYS;
    }
}

