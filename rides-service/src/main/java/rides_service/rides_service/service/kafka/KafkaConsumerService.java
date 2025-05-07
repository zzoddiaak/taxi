package rides_service.rides_service.service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import rides_service.rides_service.service.api.RideService;

@Service
public class KafkaConsumerService {

    private final RideService rideService;

    public KafkaConsumerService(RideService rideService) {
        this.rideService = rideService;
    }

    @KafkaListener(topics = "ride-acceptance", groupId = "ride-service-group")
    public void listenRideAcceptance(ConsumerRecord<String, String> record) {
        processMessage(record);
        String[] parts = record.value().split(":");
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
    public void listenRideStart(ConsumerRecord<String, String> record) {
        processMessage(record);
        String rideId = record.value();
        rideService.updateRideStatus(Long.parseLong(rideId), "IN_PROGRESS");
        System.out.println("Ride " + rideId + " started");
    }

    @KafkaListener(topics = "ride-end", groupId = "ride-service-group")
    public void listenRideEnd(ConsumerRecord<String, String> record) {
        processMessage(record);
        String rideId = record.value();
        rideService.updateRideStatus(Long.parseLong(rideId), "COMPLETED");
        System.out.println("Ride " + rideId + " ended");
    }

    private void processMessage(ConsumerRecord<String, String> record) {
        String token = getTokenFromHeaders(record);
        if (token != null) {
            Jwt jwt = Jwt.withTokenValue(token)
                    .header("alg", "RS256")
                    .claim("sub", "service-account")
                    .build();

            SecurityContextHolder.getContext().setAuthentication(
                    new JwtAuthenticationToken(jwt)
            );
        }
    }

    private String getTokenFromHeaders(ConsumerRecord<String, String> record) {
        Iterable<org.apache.kafka.common.header.Header> headers = record.headers().headers("Authorization");
        if (headers.iterator().hasNext()) {
            return new String(headers.iterator().next().value()).replace("Bearer ", "");
        }
        return null;
    }
}