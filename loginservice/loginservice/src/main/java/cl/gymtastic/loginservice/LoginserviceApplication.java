package cl.gymtastic.loginservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration; // <-- 1. IMPORTAR
import org.springframework.cloud.openfeign.EnableFeignClients; // <-- 2. IMPORTAR

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // <-- 1. EXCLUIR BD
@EnableFeignClients // <-- 2. HABILITAR CLIENTE FEIGN
public class LoginserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginserviceApplication.class, args);
	}

}