package passenger_service.passenger_service.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import passenger_service.passenger_service.dto.rating.PassengerRatingRequestDto;
import passenger_service.passenger_service.dto.rating.RatingDto;
import passenger_service.passenger_service.dto.rating.RatingUpdateDto;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

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
        kafkaTemplate.send("passenger-rating-topic", message);
    }
}