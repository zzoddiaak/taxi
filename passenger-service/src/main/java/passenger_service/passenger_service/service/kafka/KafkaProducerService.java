package passenger_service.passenger_service.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import passenger_service.passenger_service.dto.rating.PassengerRatingRequestDto;
import passenger_service.passenger_service.dto.rating.RatingDto;
import passenger_service.passenger_service.dto.rating.RatingUpdateDto;

@Service
@RequiredArgsConstructor
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

    public void rateDriver(Long passengerId, Long driverId, Float rating) {
        PassengerRatingRequestDto ratingRequestDto = PassengerRatingRequestDto.builder()
                .passengerId(passengerId)
                .driverId(driverId)
                .rating(rating)
                .build();

        sendRating(ratingRequestDto);
    }

    public void sendRating(PassengerRatingRequestDto ratingRequestDto) {
        String message = ratingRequestDto.getPassengerId() + ":" + ratingRequestDto.getDriverId() + ":" + ratingRequestDto.getRating();
        kafkaTemplate.send(createMessageWithToken("passenger-rating-topic", message));
    }
}