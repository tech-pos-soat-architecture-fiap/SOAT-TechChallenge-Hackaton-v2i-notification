package br.com.fiap.v2i.notification.queue;

import br.com.fiap.v2i.notification.VideoNotificationMessage;
import br.com.fiap.v2i.notification.queue.QueueConfig;
import br.com.fiap.v2i.notification.queue.QueueController;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QueueController.class)
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void testNotification__with_defaults_should_publish_success_message() throws Exception {
        mockMvc.perform(post("/queue/test-notification"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Message sent to queue")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(QueueConfig.QUEUE_NAME)));

        verify(rabbitTemplate).convertAndSend(
                eq(QueueConfig.EXCHANGE_NAME),
                eq(QueueConfig.ROUTING_KEY),
                argThat((VideoNotificationMessage m) ->
                        "test-video-id".equals(m.getVideoId())
                                && "user@example.com".equals(m.getUserEmail())
                                && "SUCCESS".equals(m.getStatus())
                                && m.getOutputUrl() != null
                                && m.getErrorMessage() == null
                )
        );
    }

    @Test
    void testNotification__with_custom_params_should_publish_message() throws Exception {
        mockMvc.perform(post("/queue/test-notification")
                        .param("videoId", "custom-video-1")
                        .param("userEmail", "custom@test.com")
                        .param("status", "FAILURE"))
                .andExpect(status().isOk());

        verify(rabbitTemplate).convertAndSend(
                eq(QueueConfig.EXCHANGE_NAME),
                eq(QueueConfig.ROUTING_KEY),
                argThat((VideoNotificationMessage m) ->
                        "custom-video-1".equals(m.getVideoId())
                                && "custom@test.com".equals(m.getUserEmail())
                                && "FAILURE".equals(m.getStatus())
                                && m.getOutputUrl() == null
                                && "Processing timed out".equals(m.getErrorMessage())
                )
        );
    }
}
