package cl.gymtastic.checkoutservice.service;

import cl.gymtastic.checkoutservice.client.ProductClient;
import cl.gymtastic.checkoutservice.client.UserClient;
import cl.gymtastic.checkoutservice.dto.*;
import cl.gymtastic.checkoutservice.model.Order;
import cl.gymtastic.checkoutservice.repository.OrderRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    // --- INYECCIÓN DEL REPOSITORIO NUEVO ---
    @Autowired
    private OrderRepository orderRepository;

    private final int THRESHOLD_DAYS = 3; 

    public Map<String, Object> processCheckout(CheckoutRequest request) {
        
        boolean hasPlanInCart = request.getItems().stream().anyMatch(item -> "plan".equals(item.getTipo()));
        List<CartItemDto> merchItems = request.getItems().stream()
                .filter(item -> "merch".equals(item.getTipo()))
                .collect(Collectors.toList());

        // --- 1. Validar Plan ---
        if (hasPlanInCart) {
            if (request.getSede() == null) {
                throw new CheckoutException("Selecciona una sede para asociar tu plan.");
            }
            ResponseEntity<UserProfileResponse> userResponse = userClient.getUserProfile(request.getUserEmail());
            if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
                throw new CheckoutException("Usuario no encontrado.");
            }
            UserProfileResponse user = userResponse.getBody();
            if (!canBuyPlan(user.getPlanEndMillis())) {
                long remainingDays = TimeUnit.MILLISECONDS.toDays(user.getPlanEndMillis() - System.currentTimeMillis());
                throw new CheckoutException("Ya tienes un plan activo. Días restantes: " + remainingDays);
            }
        }

        // --- 2. Descontar Stock ---
        if (!merchItems.isEmpty()) {
            try {
                ResponseEntity<Map<String, String>> stockResponse = productClient.decreaseStock(new StockDecreaseRequest(merchItems));
                if (!stockResponse.getStatusCode().is2xxSuccessful()) {
                    throw new CheckoutException("Stock insuficiente para uno o más productos.");
                }
            } catch (FeignException e) {
                throw new CheckoutException("Error al descontar stock: " + e.contentUTF8());
            }
        }

        // --- 3. Activar Plan ---
        if (hasPlanInCart) {
            SedeDto sede = request.getSede();
            SubscriptionUpdateRequest subRequest = new SubscriptionUpdateRequest();
            long newPlanEnd = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30);
            
            subRequest.setPlanEndMillis(newPlanEnd);
            subRequest.setSedeId(sede.getId());
            subRequest.setSedeName(sede.getNombre());
            subRequest.setSedeLat(sede.getLat());
            subRequest.setSedeLng(sede.getLng());
            
            userClient.updateSubscription(request.getUserEmail(), subRequest);
        }

        // --- 4. GUARDAR EN HISTORIAL (NUEVA LÓGICA) ---
        saveOrderHistory(request);

        return Map.of(
            "success", true,
            "message", "Compra procesada exitosamente.",
            "planActivated", hasPlanInCart
        );
    }

    // --- Métodos Privados ---

    private void saveOrderHistory(CheckoutRequest request) {
        // 1. Crear descripción legible (Ej: "Plan GymTastic, Proteína Whey x2")
        String description = request.getItems().stream()
                .map(item -> item.getNombre() + (item.getCantidad() > 1 ? " x" + item.getCantidad() : ""))
                .collect(Collectors.joining(", "));

        // 2. Calcular total (Asumiendo que CartItemDto tiene getPrecio())
        double total = request.getItems().stream()
                .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                .sum();

        // 3. Guardar en BD
        Order order = new Order(
                request.getUserEmail(),
                LocalDateTime.now(),
                description,
                total
        );
        orderRepository.save(order);
    }

    // --- Método Público para consultar Historial ---
    public List<OrderDto> getOrderHistory(String email) {
        List<Order> orders = orderRepository.findByUserEmailOrderByDateDesc(email);
        
        // Convertir Entidad -> DTO
        return orders.stream()
                .map(o -> new OrderDto(o.getId(), o.getDescription(), o.getTotalAmount(), o.getDate()))
                .collect(Collectors.toList());
    }
    
    private boolean canBuyPlan(Long planEndMillis) {
        if (planEndMillis == null) return true;
        long diff = planEndMillis - System.currentTimeMillis();
        if (diff <= 0) return true;
        long daysRemaining = TimeUnit.MILLISECONDS.toDays(diff);
        return daysRemaining <= THRESHOLD_DAYS;
    }
}