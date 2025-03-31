package driver_service.driver_service.integration.kafka;

import driver_service.driver_service.integration.config.TestContainersInitializer;
import driver_service.driver_service.service.kafka.KafkaConsumerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestContainersInitializer.class)
class KafkaConsumerServiceIntegrationTest {

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    @Autowired
    private KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Test
    void listenRideStart_ShouldProcessMessage() {
        String rideId = "123";
        kafkaConsumer.subscribe(Collections.singletonList("ride-start"));

        kafkaProducer.send(new ProducerRecord<>("ride-start", rideId));
        kafkaProducer.flush();

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(rideId, record.value());
    }

    @Test
    void listenRideEnd_ShouldProcessMessage() {
        String rideId = "123";
        kafkaConsumer.subscribe(Collections.singletonList("ride-end"));

        kafkaProducer.send(new ProducerRecord<>("ride-end", rideId));
        kafkaProducer.flush();

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(rideId, record.value());
    }

    @Test
    void listenRideAcceptance_ShouldProcessMessage() {
        String message = "123:456:true";
        kafkaConsumer.subscribe(Collections.singletonList("ride-acceptance"));

        kafkaProducer.send(new ProducerRecord<>("ride-acceptance", message));
        kafkaProducer.flush();

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(message, record.value());
    }

    @Test
    void listenPassengerRating_ShouldProcessMessage() {
        String message = "1:2:5.0";
        kafkaConsumer.subscribe(Collections.singletonList("passenger-rating-topic"));

        kafkaProducer.send(new ProducerRecord<>("passenger-rating-topic", message));
        kafkaProducer.flush();

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(message, record.value());
    }
}