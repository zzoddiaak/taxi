package rides_service.rides_service.dto.passenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    private Double averageRating;
    private Integer ratingCount;
}
