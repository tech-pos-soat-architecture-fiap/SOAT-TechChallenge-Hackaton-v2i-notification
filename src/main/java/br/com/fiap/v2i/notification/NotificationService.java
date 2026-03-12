package br.com.fiap.v2i.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void handleNotification(VideoNotificationMessage message) {
        if (message == null) {
            logger.warn("Received a null notification message, skipping.");
            return;
        }

        logger.info("Processing notification for videoId='{}', status='{}', userEmail='{}'",
                message.getVideoId(), message.getStatus(), message.getUserEmail());

        switch (message.getStatus()) {
            case "SUCCESS" -> notifySuccess(message);
            case "FAILURE" -> notifyFailure(message);
            default -> logger.warn("Unknown status '{}' for videoId='{}'. No notification sent.",
                    message.getStatus(), message.getVideoId());
        }
    }

    private void notifySuccess(VideoNotificationMessage message) {
        logger.info("[NOTIFY] Video '{}' processed successfully. Notifying user '{}'.",
                message.getVideoId(), message.getUserEmail());
        emailService.sendSuccessEmail(message.getUserEmail(), message.getVideoId(), message.getOutputUrl());
    }

    private void notifyFailure(VideoNotificationMessage message) {
        logger.error("[NOTIFY] Video '{}' processing failed. Notifying user '{}'. Reason: {}",
                message.getVideoId(), message.getUserEmail(), message.getErrorMessage());
        emailService.sendFailureEmail(message.getUserEmail(), message.getVideoId(), message.getErrorMessage());
    }

}
