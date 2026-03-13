package br.com.fiap.v2i.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void triggerNotification__should_return_200_and_trigger_service() throws Exception {
        VideoNotificationMessage message = new VideoNotificationMessage(
                "video-123", "user@example.com", "SUCCESS",
                "https://example.com/output/video-123.zip", null
        );

        mockMvc.perform(post("/notifications/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("triggered"))
                .andExpect(jsonPath("$.videoId").value("video-123"));

        verify(notificationService).handleNotification(any(VideoNotificationMessage.class));
    }

    @Test
    void triggerNotification__should_accept_and_return_200_when_status_is_failure() throws Exception {
        VideoNotificationMessage message = new VideoNotificationMessage(
                "video-int-2", "user@test.com", "FAILURE",
                null, "Error message"
        );

        mockMvc.perform(post("/notifications/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value("video-int-2"));
    }

    @Test
    void triggerNotification__should_return_unknown_response_when_video_id_is_null() throws Exception {
        VideoNotificationMessage message = new VideoNotificationMessage(
                null, "user@example.com", "SUCCESS", null, null
        );

        mockMvc.perform(post("/notifications/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("triggered"))
                .andExpect(jsonPath("$.videoId").value("unknown"));

        verify(notificationService).handleNotification(any(VideoNotificationMessage.class));
    }

}
