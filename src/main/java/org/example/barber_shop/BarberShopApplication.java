package org.example.barber_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BarberShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarberShopApplication.class, args);
	}
}
