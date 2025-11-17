package cl.gymtastic.product_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema; // Importar Schema

// Mapea tu ProductEntity.kt
@Entity
@Table(name = "products")
@Data // Lombok: genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: genera constructor sin argumentos
@AllArgsConstructor // Lombok: genera constructor con todos los argumentos
@Builder // Lombok: permite construir objetos con .builder()
@Schema(name = "Product", description = "Entidad que representa un producto o plan")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del producto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id; // Mapea 'id: Int' de Kotlin

    @Column(nullable = false)
    @Schema(description = "Nombre del producto o plan", example = "Plan Mensual")
    private String nombre;
    
    @Column(length = 1024) // Más espacio para descripción
    @Schema(description = "Descripción detallada", example = "Polera oficial de polyester")
    private String descripcion;
    
    @Column(nullable = false)
    @Schema(description = "Precio en CLP", example = "19990.0")
    private Double precio;
    
    @Schema(description = "Stock disponible (solo para 'merch')", example = "90")
    private Integer stock;
    
    @Column(length = 512) // Espacio para la URI/URL de la imagen
    @Schema(description = "URL o URI de la imagen", example = "android.resource://...")
    private String img;
    
    @Column(nullable = false)
    @Schema(description = "Tipo de producto ('plan' o 'merch')", example = "plan")
    private String tipo;
}