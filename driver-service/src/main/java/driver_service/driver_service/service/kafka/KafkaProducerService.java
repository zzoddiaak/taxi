package driver_service.driver_service.service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRideStart(String rideId) {
        kafkaTemplate.send("ride-start", rideId);
    }

    public void sendRideEnd(String rideId) {
        kafkaTemplate.send("ride-end", rideId);
    }

    public void sendRideAcceptance(String rideId, String driverId, boolean accepted) {
        String message = rideId + ":" + driverId + ":" + accepted;
        kafkaTemplate.send("ride-acceptance", message);
    }
}