package passenger_service.passenger_service.integration.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import passenger_service.passenger_service.service.kafka.KafkaProducerService;
import passenger_service.passenger_service.integration.config.TestContainersInitializer;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EmbeddedKafka(topics = {"passenger-rating-topic"})
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestContainersInitializer.class)
@DirtiesContext
class KafkaProducerServiceIntegrationTest {

    @Autowired
    private KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Test
    void rateDriver_ShouldSendMessageToKafka() {
        String message = "1:2:5.0";
        kafkaConsumer.subscribe(Collections.singletonList("passenger-rating-topic"));

        kafkaProducerService.rateDriver(1L, 2L, 5.0f);

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(message, record.value());
    }
}