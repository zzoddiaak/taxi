package rating_service.rating_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequestDto {

    private Long driverId;
    private Long passengerId;
    private Float rating;
    private String comment;
}
