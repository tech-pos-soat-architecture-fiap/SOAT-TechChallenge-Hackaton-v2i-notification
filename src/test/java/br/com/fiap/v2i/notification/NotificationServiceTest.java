package br.com.fiap.v2i.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailService emailService;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(emailService);
    }

    @Test
    void handleNotification__should_not_call_when_message_is_null() {
        notificationService.handleNotification(null);

        verify(emailService, never()).sendSuccessEmail(any(), any(), any());
        verify(emailService, never()).sendFailureEmail(any(), any(), any());
    }

    @Test
    void handleNotification__should_send_success_email_when_status_is_success() {
        VideoNotificationMessage message = new VideoNotificationMessage(
                "video-1", "user@example.com", "SUCCESS",
                "https://example.com/output/video-1.zip", null
        );

        notificationService.handleNotification(message);

        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> videoIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendSuccessEmail(toCaptor.capture(), videoIdCaptor.capture(), urlCaptor.capture());
        assertThat(toCaptor.getValue()).isEqualTo("user@example.com");
        assertThat(videoIdCaptor.getValue()).isEqualTo("video-1");
        assertThat(urlCaptor.getValue()).isEqualTo("https://example.com/output/video-1.zip");
    }

    @Test
    void handleNotification__should_send_failure_email_when_status_is_failure() {
        VideoNotificationMessage message = new VideoNotificationMessage(
                "video-2", "user@example.com", "FAILURE",
                null, "Processing timed out"
        );

        notificationService.handleNotification(message);

        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> videoIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> errorCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendFailureEmail(toCaptor.capture(), videoIdCaptor.capture(), errorCaptor.capture());
        assertThat(toCaptor.getValue()).isEqualTo("user@example.com");
        assertThat(videoIdCaptor.getValue()).isEqualTo("video-2");
        assertThat(errorCaptor.getValue()).isEqualTo("Processing timed out");
    }

    @Test
    void handleNotification__should_not_send_email_when_status_is_unknown() {
        VideoNotificationMessage message = new VideoNotificationMessage(
                "video-3", "user@example.com", "PENDING",
                null, null
        );

        notificationService.handleNotification(message);

        verify(emailService, never()).sendSuccessEmail(any(), any(), any());
        verify(emailService, never()).sendFailureEmail(any(), any(), any());
    }
}
