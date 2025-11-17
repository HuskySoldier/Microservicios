package cl.gymtastic.product_service.config;

import cl.gymtastic.product_service.model.Product;
import cl.gymtastic.product_service.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            // Solo poblamos si la tabla está vacía
            if (repository.count() == 0) {
                System.out.println("Poblando base de datos con productos iniciales...");
                
                String poleraImgUri = "android.resource://cl.gymtastic.app/drawable/polera_gym";
                String botellaImgUri = "android.resource://cl.gymtastic.app/drawable/botella_agua";

                repository.saveAll(List.of(
                    Product.builder().nombre("Plan Mensual").precio(19990.0).tipo("plan").stock(999).build(),
                    Product.builder().nombre("Plan Trimestral").precio(54990.0).tipo("plan").stock(999).build(),
                    Product.builder().nombre("Polera Gym").precio(12990.0).tipo("merch").stock(90).img(poleraImgUri).descripcion("Polera oficial de polyester").build(),
                    Product.builder().nombre("Botella").precio(6990.0).tipo("merch").stock(80).img(botellaImgUri).descripcion("Botella de agua 1L").build()
                ));
                System.out.println("--> Base de datos poblada con Productos.");
            }
        };
    }
}