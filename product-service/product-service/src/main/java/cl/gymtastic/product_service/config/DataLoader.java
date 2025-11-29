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
                
                // --- RUTAS A TUS IMÁGENES LOCALES (en carpeta public/imagenes) ---
                String imgMensual = "/imagenes/Mensual.png";
                String imgTrimestral = "/imagenes/Trimestral.png";
                String imgPolera = "/imagenes/Polera.png";
                String imgProteina = "/imagenes/Proteina.png"; 
                String imgMancuernas = "/imagenes/Mancuernas.jpg";
                String imgPesaRusa = "/imagenes/PesaRusa.png";

                repository.saveAll(List.of(
                    // PLANES
                    Product.builder().nombre("Plan Mensual").precio(19990.0).tipo("plan").stock(999).img(imgMensual).descripcion("Acceso total por 1 mes").build(),
                    Product.builder().nombre("Plan Trimestral").precio(54990.0).tipo("plan").stock(999).img(imgTrimestral).descripcion("Ahorra con el plan de 3 meses").build(),
                    
                    // PRODUCTOS (MERCH)
                    Product.builder().nombre("Polera Gymtastic").precio(12990.0).tipo("merch").stock(90).img(imgPolera).descripcion("[Ropa] Polera oficial de entrenamiento respirable").build(),
                    Product.builder().nombre("Whey Protein").precio(45990.0).tipo("merch").stock(50).img(imgProteina).descripcion("[Suplementos] Proteína de suero de leche 1kg").build(),
                    Product.builder().nombre("Mancuernas 5kg").precio(15990.0).tipo("merch").stock(20).img(imgMancuernas).descripcion("[Equipamiento] Par de mancuernas hexagonales").build(),
                    Product.builder().nombre("Pesa Rusa 12kg").precio(22990.0).tipo("merch").stock(15).img(imgPesaRusa).descripcion("[Equipamiento] Kettlebell de hierro fundido").build()
                ));
                System.out.println("--> Base de datos poblada con Productos e Imágenes.");
            }
        };
    }
}