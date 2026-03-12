package br.com.fiap.v2i.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Manually trigger a notification (useful for admin/testing purposes).
     */
    @PostMapping("/trigger")
    public ResponseEntity<Map<String, String>> triggerNotification(
            @RequestBody VideoNotificationMessage message) {

        notificationService.handleNotification(message);
        return ResponseEntity.ok(Map.of(
                "status", "triggered",
                "videoId", message.getVideoId() != null ? message.getVideoId() : "unknown"
        ));
    }
}
