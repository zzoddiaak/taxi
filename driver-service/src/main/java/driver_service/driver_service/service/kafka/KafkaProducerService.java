package driver_service.driver_service.service.kafka;

import driver_service.driver_service.dto.rating.DriverRatingRequestDto;
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

    public void sendRating(DriverRatingRequestDto ratingRequestDto) {
        String message = ratingRequestDto.getDriverId() + ":" + ratingRequestDto.getPassengerId() + ":" + ratingRequestDto.getRating();
        kafkaTemplate.send("passenger-rating-topic", message);
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