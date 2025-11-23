package cl.gymtastic.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
	// Visita http://localhost:8082/swagger-ui/index.html para ver la API
	// gymtastic_attendance
	// gymtastic_bookings
	// gymtastic_checkout
	// gymtastic_product
	// gymtastic_traiers
	// gymtastic_users
}