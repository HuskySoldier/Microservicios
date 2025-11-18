package cl.gymtastic.checkoutservice.service;

/**
 * Excepción personalizada para manejar errores de lógica de negocio
 * durante el proceso de checkout.
 * Esto permite al controlador (Controller) atraparla y devolver
 * un error HTTP 409 (Conflict) en lugar de un 500 (Internal Server Error).
 */
public class CheckoutException extends RuntimeException {
    
    public CheckoutException(String message) {
        super(message);
    }
    
    public CheckoutException(String message, Throwable cause) {
        super(message, cause);
    }
}