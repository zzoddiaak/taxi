package rating_service.rating_service.component;

import rating_service.rating_service.dto.RatingResponseDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TestContext {
    private RatingResponseDto ratingResponse;
    private Long ratingId;
    private Long driverId;
    private Long passengerId;
    private Float currentRating;
    private Double driverCurrentRating;
    private Integer driverRatingCount;
    private Double passengerCurrentRating;
    private Integer passengerRatingCount;
    private boolean ratingDeleted;
}