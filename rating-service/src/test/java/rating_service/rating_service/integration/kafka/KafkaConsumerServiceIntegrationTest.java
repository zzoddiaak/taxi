package rating_service.rating_service.integration.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import rating_service.rating_service.service.kafka.KafkaConsumerService;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EmbeddedKafka(topics = {"passenger-rating-topic", "driver-rating-topic"})
@ActiveProfiles("test")
class KafkaConsumerServiceIntegrationTest {

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    @Autowired
    private KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Test
    void listenPassengerRating_ShouldProcessMessage() {
        String message = "1:2:5.0";
        kafkaConsumer.subscribe(Collections.singletonList("passenger-rating-topic"));

        kafkaProducer.send(new ProducerRecord<>("passenger-rating-topic", message));
        kafkaProducer.flush();

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(message, record.value());

    }

    @Test
    void listenDriverRating_ShouldProcessMessage() {
        String message = "1:2:4.5";
        kafkaConsumer.subscribe(Collections.singletonList("driver-rating-topic"));

        kafkaProducer.send(new ProducerRecord<>("driver-rating-topic", message));
        kafkaProducer.flush();

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(message, record.value());

    }
}