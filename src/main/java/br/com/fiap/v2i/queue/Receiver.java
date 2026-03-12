package br.com.fiap.v2i.queue;

import br.com.fiap.v2i.notification.NotificationService;
import br.com.fiap.v2i.notification.VideoNotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private final NotificationService notificationService;

    public Receiver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = QueueConfig.QUEUE_NAME)
    public void receiveMessage(VideoNotificationMessage message) {
        logger.info("Message received from queue '{}': {}", QueueConfig.QUEUE_NAME, message);
        notificationService.handleNotification(message);
    }
}