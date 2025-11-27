package cl.gymtastic.checkoutservice.service;

import cl.gymtastic.checkoutservice.client.ProductClient;
import cl.gymtastic.checkoutservice.client.UserClient;
import cl.gymtastic.checkoutservice.dto.*;
// --- CAMBIO IMPORTANTE AQUÍ ---
import cl.gymtastic.checkoutservice.model.PurchaseOrder;
import cl.gymtastic.checkoutservice.repository.PurchaseOrderRepository;
// ------------------------------
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

    // --- CAMBIO DE NOMBRE DEL REPOSITORIO ---
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private final int THRESHOLD_DAYS = 3; 

    public Map<String, Object> processCheckout(CheckoutRequest request) {
        // ... (El código de validación de plan y stock se mantiene igual) ...
        // COPIA Y PEGA LA LÓGICA DE VALIDACIÓN ANTERIOR AQUÍ O MANTÉNLA SI YA LA TIENES
        
        boolean hasPlanInCart = request.getItems().stream().anyMatch(item -> "plan".equals(item.getTipo()));
        List<CartItemDto> merchItems = request.getItems().stream()
                .filter(item -> "merch".equals(item.getTipo()))
                .collect(Collectors.toList());

        if (hasPlanInCart) {
             if (request.getSede() == null) throw new CheckoutException("Selecciona una sede.");
             ResponseEntity<UserProfileResponse> userResponse = userClient.getUserProfile(request.getUserEmail());
             if (!userResponse.getStatusCode().is2xxSuccessful()) throw new CheckoutException("Usuario no encontrado.");
             if (!canBuyPlan(userResponse.getBody().getPlanEndMillis())) throw new CheckoutException("Ya tienes un plan activo.");
        }

        if (!merchItems.isEmpty()) {
            ResponseEntity<Map<String, String>> stockResponse = productClient.decreaseStock(new StockDecreaseRequest(merchItems));
            if (!stockResponse.getStatusCode().is2xxSuccessful()) throw new CheckoutException("Stock insuficiente.");
        }

        if (hasPlanInCart) {
            SedeDto sede = request.getSede();
            SubscriptionUpdateRequest subRequest = new SubscriptionUpdateRequest();
            subRequest.setPlanEndMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30));
            subRequest.setSedeId(sede.getId());
            subRequest.setSedeName(sede.getNombre());
            subRequest.setSedeLat(sede.getLat());
            subRequest.setSedeLng(sede.getLng());
            userClient.updateSubscription(request.getUserEmail(), subRequest);
        }

        // --- 4. GUARDAR EN HISTORIAL ---
        saveOrderHistory(request);

        return Map.of("success", true, "message", "Compra exitosa.", "planActivated", hasPlanInCart);
    }

    private void saveOrderHistory(CheckoutRequest request) {
        String description = request.getItems().stream()
                .map(item -> item.getNombre() + (item.getCantidad() > 1 ? " x" + item.getCantidad() : ""))
                .collect(Collectors.joining(", "));

        double total = request.getItems().stream()
                .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                .sum();

        // --- USAR PURCHASE ORDER ---
        PurchaseOrder order = new PurchaseOrder(
                request.getUserEmail(),
                LocalDateTime.now(),
                description,
                total,
                request.getItems().stream().mapToInt(CartItemDto::getCantidad).sum() // Sumar cantidades
        );
        purchaseOrderRepository.save(order);
    }

    public List<OrderDto> getOrderHistory(String email) {
        List<PurchaseOrder> orders = purchaseOrderRepository.findByUserEmailOrderByDateDesc(email);
        
        return orders.stream()
                .map(o -> new OrderDto(
                    o.getId(), 
                    o.getUserEmail(), // <--- Pasamos el email
                    o.getDescription(), 
                    o.getTotalAmount(), 
                    o.getDate(), 
                    o.getItemsCount()
                ))
                .collect(Collectors.toList());
    }

    public List<OrderDto> getAllOrders() {
        // findAll() viene por defecto en JpaRepository
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
        
        // Ordenamos por fecha descendente (lo más nuevo primero)
        // Nota: Si quieres que la BD ordene, podrías crear findAllByOrderByDateDesc() en el repo
        orders.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        return orders.stream()
                .map(o -> new OrderDto(
                    o.getId(), 
                    o.getUserEmail(),
                    o.getDescription(), 
                    o.getTotalAmount(), 
                    o.getDate(), 
                    o.getItemsCount()
                ))
                .collect(Collectors.toList());
    }
    
    private boolean canBuyPlan(Long planEndMillis) {
        if (planEndMillis == null) return true;
        long diff = planEndMillis - System.currentTimeMillis();
        return diff <= 0 || TimeUnit.MILLISECONDS.toDays(diff) <= THRESHOLD_DAYS;
    }
}