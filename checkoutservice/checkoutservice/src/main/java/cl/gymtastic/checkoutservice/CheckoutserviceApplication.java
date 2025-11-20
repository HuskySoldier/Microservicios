package cl.gymtastic.checkoutservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// Eliminamos el parámetro 'exclude' para permitir que la BD funcione
@SpringBootApplication
@EnableFeignClients
public class CheckoutserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckoutserviceApplication.class, args);
    }
}


// Visita http://localhost:8086/swagger-ui.html para ver la API
