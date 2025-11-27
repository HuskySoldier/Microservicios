package cl.gymtastic.checkoutservice.dto;

import java.time.LocalDateTime;

public class OrderDto {
    private Long id;
    private String userEmail; // <--- NUEVO CAMPO
    private String description;
    private double totalAmount;
    private LocalDateTime date;
    private int itemsCount;

    // Constructor actualizado
    public OrderDto(Long id, String userEmail, String description, double totalAmount, LocalDateTime date, int itemsCount) {
        this.id = id;
        this.userEmail = userEmail; // <--- Asignar
        this.description = description;
        this.totalAmount = totalAmount;
        this.date = date;
        this.itemsCount = itemsCount;
    }

    // Getters
    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; } // <--- Nuevo Getter
    public String getDescription() { return description; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getDate() { return date; }
    public int getItemsCount() { return itemsCount; }
}