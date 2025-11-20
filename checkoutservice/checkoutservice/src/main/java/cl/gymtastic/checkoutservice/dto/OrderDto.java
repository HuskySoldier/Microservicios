package cl.gymtastic.checkoutservice.dto;

import java.time.LocalDateTime;

public class OrderDto {
    private Long id;
    private String description;
    private double totalAmount;
    private LocalDateTime date;

    public OrderDto(Long id, String description, double totalAmount, LocalDateTime date) {
        this.id = id;
        this.description = description;
        this.totalAmount = totalAmount;
        this.date = date;
    }

    // Getters
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getDate() { return date; }
}