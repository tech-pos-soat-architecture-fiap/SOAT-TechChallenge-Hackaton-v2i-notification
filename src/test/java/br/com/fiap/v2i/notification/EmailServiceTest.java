package br.com.fiap.v2i.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    private static final String FROM = "noreply@test.com";

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender);
        ReflectionTestUtils.setField(emailService, "from", FROM);
    }

    @Test
    void sendSuccessEmail__should_send_mail_correctly() {
        emailService.sendSuccessEmail("user@example.com", "video-123", "https://example.com/output/video-123.zip");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();

        assertThat(message.getFrom()).isEqualTo(FROM);
        assertThat(message.getTo()).containsExactly("user@example.com");
        assertThat(message.getSubject()).isEqualTo("Your video is ready! 🎉");
        assertThat(message.getText())
                .contains("video-123")
                .contains("https://example.com/output/video-123.zip");
    }

    @Test
    void sendSuccessEmail__should_put_NA_when_outPutUrl_is_null() {
        emailService.sendSuccessEmail("user@example.com", "video-456", null);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getText()).contains("N/A");
    }

    @Test
    void sendFailureEmail__should_send_mail_correctly() {
        emailService.sendFailureEmail("user@example.com", "video-789", "Processing timed out");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();

        assertThat(message.getFrom()).isEqualTo(FROM);
        assertThat(message.getTo()).containsExactly("user@example.com");
        assertThat(message.getSubject()).isEqualTo("Video processing failed");
        assertThat(message.getText())
                .contains("video-789")
                .contains("Processing timed out");
    }

    @Test
    void sendFailureEmail__should_use_unknown_error_in_body_when_error_message_is_null() {
        emailService.sendFailureEmail("user@example.com", "video-999", null);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getText()).contains("Unknown error");
    }
}
