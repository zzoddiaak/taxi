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
import rides_service.rides_service.service.kafka.KafkaProducerService;
import rides_service.rides_service.service.kafka.KafkaConsumerService;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EmbeddedKafka(topics = {"available-rides", "ride-acceptance", "ride-start", "ride-end"})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class KafkaConsumerServiceIntegrationTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Autowired
    private KafkaConsumer<String, String> kafkaConsumer;

    @Test
    void listenRideAcceptance_ShouldUpdateRideStatus() {
        String rideId = "1";
        String driverId = "1";
        boolean accepted = true;
        String message = rideId + ":" + driverId + ":" + accepted;

        kafkaConsumer.subscribe(Collections.singletonList("ride-acceptance"));

        kafkaProducerService.sendRideAcceptance(rideId, driverId, accepted);

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(message, record.value());
    }

    @Test
    void listenRideStart_ShouldUpdateRideStatus() {
        String rideId = "1";
        kafkaConsumer.subscribe(Collections.singletonList("ride-start"));

        kafkaProducerService.sendRideStart(rideId);

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(rideId, record.value());
    }

    @Test
    void listenRideEnd_ShouldUpdateRideStatus() {
        String rideId = "1";
        kafkaConsumer.subscribe(Collections.singletonList("ride-end"));

        kafkaProducerService.sendRideEnd(rideId);

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(rideId, record.value());
    }
}