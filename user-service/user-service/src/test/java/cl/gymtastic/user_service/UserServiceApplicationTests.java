package cl.gymtastic.user_service; // <-- ¡Paquete incorrecto o diferente!

import cl.gymtastic.userservice.UserServiceApplication; // <-- Importar la clase principal
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = UserServiceApplication.class) // <-- CORRECCIÓN CLAVE
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}