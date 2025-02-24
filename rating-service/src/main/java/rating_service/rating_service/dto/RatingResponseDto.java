package rating_service.rating_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDto {

    private Long id;
    private Long driverId;
    private Long passengerId;
    private Float rating;
    private String comment;
    private LocalDateTime createdAt;
}
