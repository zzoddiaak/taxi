package rating_service.rating_service.integration.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import rating_service.rating_service.service.kafka.KafkaConsumerService;
import rating_service.rating_service.integration.config.KafkaTestContainersInitializer;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = KafkaTestContainersInitializer.class)
class KafkaConsumerServiceIntegrationTest {

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    @Autowired
    private KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Test
    void listenPassengerRating_ShouldProcessMessage() {
        String topic = "passenger-rating-topic";
        String message = "1:2:5.0";

        kafkaConsumer.subscribe(Collections.singletonList(topic));
        kafkaProducer.send(new ProducerRecord<>(topic, message));
        kafkaProducer.flush();

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(10)).iterator().next();
        assertEquals(message, record.value());
    }

    @Test
    void listenDriverRating_ShouldProcessMessage() {
        String topic = "driver-rating-topic";
        String message = "1:2:4.5";

        kafkaConsumer.subscribe(Collections.singletonList(topic));
        kafkaProducer.send(new ProducerRecord<>(topic, message));
        kafkaProducer.flush();

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(10)).iterator().next();
        assertEquals(message, record.value());
    }
}