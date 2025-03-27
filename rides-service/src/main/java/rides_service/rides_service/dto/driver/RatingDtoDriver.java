package rides_service.rides_service.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDtoDriver {
    private Double averageRating;
    private Integer ratingCount;
    private Double passengerRating;
}
