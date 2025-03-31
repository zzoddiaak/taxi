package rides_service.rides_service.integration.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import rides_service.rides_service.integration.config.TestContainersInitializer;
import rides_service.rides_service.service.kafka.KafkaProducerService;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EmbeddedKafka(topics = {"available-rides"})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(initializers = TestContainersInitializer.class)
class KafkaProducerServiceIntegrationTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaConsumer<String, String> kafkaConsumer;

    @Test
    void sendAvailableRide_ShouldSendMessageToKafka() {
        String rideId = "1";
        kafkaConsumer.subscribe(Collections.singletonList("available-rides"));

        kafkaProducerService.sendAvailableRide(rideId);

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(rideId, record.value());
    }
}