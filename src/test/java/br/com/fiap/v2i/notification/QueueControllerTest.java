package br.com.fiap.v2i.notification;

import br.com.fiap.v2i.notification.queue.QueueConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Test
    void testNotification__should_publish_to_queue_correctly() throws Exception {
        mockMvc.perform(post("/queue/test-notification")
                        .param("videoId", "int-video-1")
                        .param("userEmail", "int@test.com")
                        .param("status", "SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(QueueConfig.QUEUE_NAME)));

        verify(rabbitTemplate).convertAndSend(
                eq(QueueConfig.EXCHANGE_NAME),
                eq(QueueConfig.ROUTING_KEY),
                argThat((VideoNotificationMessage m) ->
                        "int-video-1".equals(m.getVideoId())
                                && "int@test.com".equals(m.getUserEmail())
                                && "SUCCESS".equals(m.getStatus())
                )
        );
    }
}
