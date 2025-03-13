package driver_service.driver_service.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRatingRequestDto {
    private Long driverId;
    private Long passengerId;
    private Float rating;
}