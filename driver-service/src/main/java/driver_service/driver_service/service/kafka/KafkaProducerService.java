package driver_service.driver_service.service.kafka;

import driver_service.driver_service.dto.rating.DriverRatingRequestDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private Message<String> createMessageWithToken(String topic, String payload) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = null;

        if (authentication != null && authentication.getCredentials() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getCredentials();
            token = jwt.getTokenValue();
        }

        return MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("Authorization", "Bearer " + token)
                .build();
    }

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRideStart(String rideId) {
        kafkaTemplate.send(createMessageWithToken("ride-start", rideId));
    }

    public void sendRideEnd(String rideId) {
        kafkaTemplate.send(createMessageWithToken("ride-end", rideId));
    }

    public void sendRideAcceptance(String rideId, String driverId, boolean accepted) {
        String message = rideId + ":" + driverId + ":" + accepted;
        kafkaTemplate.send( createMessageWithToken("ride-acceptance", message));
    }

    public void sendRating(DriverRatingRequestDto ratingRequestDto) {
        String message = ratingRequestDto.getDriverId() + ":" + ratingRequestDto.getPassengerId() + ":" + ratingRequestDto.getRating();
        kafkaTemplate.send(createMessageWithToken("passenger-rating-topic", message));
    }

    public void ratePassenger(Long driverId, Long passengerId, Float rating) {
        DriverRatingRequestDto ratingRequestDto = DriverRatingRequestDto.builder()
                .driverId(driverId)
                .passengerId(passengerId)
                .rating(rating)
                .build();

        sendRating(ratingRequestDto);
    }
}