package rating_service.rating_service.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import rating_service.rating_service.dto.RatingEvent;
import rating_service.rating_service.dto.RatingRequestDto;
import rating_service.rating_service.service.api.RatingService;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RatingService ratingService;

    @KafkaListener(topics = "passenger-rating-topic", groupId = "rating-service-group")
    public void listenPassengerRating(String message) {
        String[] parts = message.split(":");
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
    public void listenDriverRating(String message) {
        String[] parts = message.split(":");
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

}