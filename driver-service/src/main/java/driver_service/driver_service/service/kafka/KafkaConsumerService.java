package driver_service.driver_service.service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "available-rides", groupId = "driver-service-group")
    public void listenAvailableRides(ConsumerRecord<String, String> record) {
        processMessage(record);
        System.out.println("Available ride: " + record.value());
    }

    @KafkaListener(topics = "ride-acceptance", groupId = "driver-service-group")
    public void listenRideAcceptance(ConsumerRecord<String, String> record) {
        processMessage(record);
        String[] parts = record.value().split(":");
        String rideId = parts[0];
        String driverId = parts[1];
        boolean accepted = Boolean.parseBoolean(parts[2]);

        if (accepted) {
            System.out.println("Driver " + driverId + " accepted ride " + rideId);
        } else {
            System.out.println("Driver " + driverId + " declined ride " + rideId);
        }
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