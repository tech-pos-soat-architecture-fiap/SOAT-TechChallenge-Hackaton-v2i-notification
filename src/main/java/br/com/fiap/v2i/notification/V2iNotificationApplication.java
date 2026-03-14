package br.com.fiap.v2i.notification;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class V2iNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(V2iNotificationApplication.class, args);
	}

}
