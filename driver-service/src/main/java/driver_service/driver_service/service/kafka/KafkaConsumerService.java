package driver_service.driver_service.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "available-rides", groupId = "driver-service-group")
    public void listenAvailableRides(String rideId) {
        System.out.println("Available ride: " + rideId);
    }

    @KafkaListener(topics = "ride-acceptance", groupId = "driver-service-group")
    public void listenRideAcceptance(String message) {
        String[] parts = message.split(":");
        String rideId = parts[0];
        String driverId = parts[1];
        boolean accepted = Boolean.parseBoolean(parts[2]);

        if (accepted) {
            System.out.println("Driver " + driverId + " accepted ride " + rideId);
        } else {
            System.out.println("Driver " + driverId + " declined ride " + rideId);
        }
    }
}