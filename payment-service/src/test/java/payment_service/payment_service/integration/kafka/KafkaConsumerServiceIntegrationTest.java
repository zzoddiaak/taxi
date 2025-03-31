package payment_service.payment_service.integration.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import payment_service.payment_service.service.kafka.KafkaConsumerService;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EmbeddedKafka(topics = {"ride-completed"})
@ActiveProfiles("test")
class KafkaConsumerServiceIntegrationTest {

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    @Autowired
    private KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Test
    void listenRideCompleted_ShouldProcessMessage() {
        String message = "1:1:100";
        kafkaConsumer.subscribe(Collections.singletonList("ride-completed"));

        kafkaProducer.send(new ProducerRecord<>("ride-completed", message));
        kafkaProducer.flush();

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertEquals(message, record.value());
    }
}