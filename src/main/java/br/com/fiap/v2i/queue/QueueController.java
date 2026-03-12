package br.com.fiap.v2i.queue;

import br.com.fiap.v2i.notification.VideoNotificationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
public class QueueController {

    private final RabbitTemplate rabbitTemplate;

    public QueueController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Test endpoint: publishes a sample VideoNotificationMessage to the notification queue.
     * Remove or secure this endpoint before going to production.
     */
    @PostMapping("/test-notification")
    public ResponseEntity<String> testNotification(
            @RequestParam(defaultValue = "test-video-id") String videoId,
            @RequestParam(defaultValue = "user@example.com") String userEmail,
            @RequestParam(defaultValue = "SUCCESS") String status) {

        VideoNotificationMessage message = new VideoNotificationMessage(
                videoId, userEmail, status,
                status.equals("SUCCESS") ? "https://example.com/output/" + videoId + ".zip" : null,
                status.equals("FAILURE") ? "Processing timed out" : null
        );

        rabbitTemplate.convertAndSend(QueueConfig.EXCHANGE_NAME, QueueConfig.ROUTING_KEY, message);
        return ResponseEntity.ok("Message sent to queue '" + QueueConfig.QUEUE_NAME + "': " + message);
    }
}
