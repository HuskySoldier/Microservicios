package cl.gymtastic.checkoutservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 500)
    private String description;

    private double totalAmount;
    
    // --- NUEVO CAMPO ---
    private int itemsCount; 

    public PurchaseOrder() {}

    // Actualizamos el constructor para recibir el itemsCount
    public PurchaseOrder(String userEmail, LocalDateTime date, String description, double totalAmount, int itemsCount) {
        this.userEmail = userEmail;
        this.date = date;
        this.description = description;
        this.totalAmount = totalAmount;
        this.itemsCount = itemsCount; // <--- Asignar
    }

    // Getters y Setters existentes...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    // --- NUEVOS GETTER Y SETTER ---
    public int getItemsCount() { return itemsCount; }
    public void setItemsCount(int itemsCount) { this.itemsCount = itemsCount; }
}