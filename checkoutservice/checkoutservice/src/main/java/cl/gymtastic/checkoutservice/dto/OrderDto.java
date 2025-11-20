package cl.gymtastic.checkoutservice.dto;

import java.time.LocalDateTime;

public class OrderDto {
    private Long id;
    private String description;
    private double totalAmount;
    private LocalDateTime date;
    private int itemsCount; // <--- NUEVO

    // Actualizamos constructor
    public OrderDto(Long id, String description, double totalAmount, LocalDateTime date, int itemsCount) {
        this.id = id;
        this.description = description;
        this.totalAmount = totalAmount;
        this.date = date;
        this.itemsCount = itemsCount; // <--- Asignar
    }

    // Getters
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getDate() { return date; }
    
    // --- NUEVO GETTER (Spring Boot lo usará para crear el JSON "itemsCount") ---
    public int getItemsCount() { return itemsCount; }
}