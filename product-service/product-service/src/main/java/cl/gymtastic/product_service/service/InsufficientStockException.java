package cl.gymtastic.product_service.service;

/**
 * Excepción personalizada para manejar errores de lógica de negocio
 * cuando el stock de un producto no es suficiente durante una transacción.
 */
public class InsufficientStockException extends RuntimeException {
    
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}