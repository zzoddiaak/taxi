package passenger_service.passenger_service.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRatingRequestDto {
    private Long passengerId;
    private Long driverId;
    private Float rating;
}