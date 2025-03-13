package rides_service.rides_service.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import rides_service.rides_service.service.api.RideService;

@Service
public class KafkaConsumerService {

    private final RideService rideService;

    public KafkaConsumerService(RideService rideService) {
        this.rideService = rideService;
    }

    @KafkaListener(topics = "ride-acceptance", groupId = "ride-service-group")
    public void listenRideAcceptance(String message) {
        String[] parts = message.split(":");
        String rideId = parts[0];
        String driverId = parts[1];
        boolean accepted = Boolean.parseBoolean(parts[2]);

        if (accepted) {
            rideService.updateRideStatus(Long.parseLong(rideId), "ACCEPTED");
            System.out.println("Driver " + driverId + " accepted ride " + rideId);
        } else {
            rideService.updateRideStatus(Long.parseLong(rideId), "DECLINED");
            System.out.println("Driver " + driverId + " declined ride " + rideId);
        }
    }

    @KafkaListener(topics = "ride-start", groupId = "ride-service-group")
    public void listenRideStart(String rideId) {
        rideService.updateRideStatus(Long.parseLong(rideId), "IN_PROGRESS");
        System.out.println("Ride " + rideId + " started");
    }

    @KafkaListener(topics = "ride-end", groupId = "ride-service-group")
    public void listenRideEnd(String rideId) {
        rideService.updateRideStatus(Long.parseLong(rideId), "COMPLETED");
        System.out.println("Ride " + rideId + " ended");
    }
}
