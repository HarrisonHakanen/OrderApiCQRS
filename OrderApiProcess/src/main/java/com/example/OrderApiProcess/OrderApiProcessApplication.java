package com.example.OrderApiProcess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class OrderApiProcessApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApiProcessApplication.class, args);
	}

}
