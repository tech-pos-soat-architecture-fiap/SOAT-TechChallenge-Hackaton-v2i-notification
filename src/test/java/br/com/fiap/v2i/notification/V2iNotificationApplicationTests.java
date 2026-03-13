package br.com.fiap.v2i.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;

@SpringBootTest
class V2iNotificationApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	static class MailTestConfig {
		@Bean
		JavaMailSender javaMailSender() {
			return mock(JavaMailSender.class);
		}
	}
}
