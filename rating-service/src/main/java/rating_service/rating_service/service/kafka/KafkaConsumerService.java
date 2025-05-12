package rating_service.rating_service.service.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import rating_service.rating_service.dto.RatingEvent;
import rating_service.rating_service.dto.RatingRequestDto;
import rating_service.rating_service.service.api.RatingService;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RatingService ratingService;

    @KafkaListener(topics = "passenger-rating-topic", groupId = "rating-service-group")
    public void listenPassengerRating(ConsumerRecord<String, String> record) {
        processMessage(record);
        String[] parts = record.value().split(":");
        Long passengerId = Long.parseLong(parts[0]);
        Long driverId = Long.parseLong(parts[1]);
        Float rating = Float.parseFloat(parts[2]);

        RatingRequestDto ratingRequestDto = RatingRequestDto.builder()
                .driverId(driverId)
                .passengerId(passengerId)
                .rating(rating)
                .comment("Rating from passenger")
                .build();

        ratingService.createRating(ratingRequestDto);
    }

    @KafkaListener(topics = "driver-rating-topic", groupId = "rating-service-group")
    public void listenDriverRating(ConsumerRecord<String, String> record) {
        processMessage(record);
        String[] parts = record.value().split(":");
        Long driverId = Long.parseLong(parts[0]);
        Long passengerId = Long.parseLong(parts[1]);
        Float rating = Float.parseFloat(parts[2]);

        RatingRequestDto ratingRequestDto = RatingRequestDto.builder()
                .driverId(driverId)
                .passengerId(passengerId)
                .rating(rating)
                .comment("Rating from driver")
                .build();

        ratingService.createRating(ratingRequestDto);
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