package br.com.fiap.v2i.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final Environment environment;

    @Value("${notification.mail.from}")
    private String from;

    public EmailService(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.environment = environment;
    }

    private boolean isProduction() {
        return Arrays.asList(environment.getActiveProfiles()).contains("production");
    }

    public void sendSuccessEmail(String to, String videoId, String outputUrl) {
        String subject = "Your video is ready! \uD83C\uDF89";
        String body = String.format(
                "Hello,%n%n" +
                "Great news! Your video (ID: %s) has been processed successfully.%n%n" +
                "You can download it here:%n%s%n%n" +
                "Thank you for using our platform!%n",
                videoId, outputUrl != null ? outputUrl : "N/A"
        );
        send(to, subject, body);
    }

    public void sendFailureEmail(String to, String videoId, String errorMessage) {
        String subject = "Video processing failed";
        String body = String.format(
                "Hello,%n%n" +
                "Unfortunately, your video (ID: %s) could not be processed.%n%n" +
                "Reason: %s%n%n" +
                "Please try again or contact support if the issue persists.%n",
                videoId, errorMessage != null ? errorMessage : "Unknown error"
        );
        send(to, subject, body);
    }

    private void send(String to, String subject, String body) {
        if (!isProduction()) {
            logger.info("[EMAIL - NON-PROD] From: '{}' | To: '{}' | Subject: '{}'\n{}", from, to, subject, body);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("Email sent to '{}' with subject '{}'", to, subject);
        } catch (MailException e) {
            logger.error("Failed to send email to '{}' with subject '{}': {}", to, subject, e.getMessage(), e);
        }
    }
}

