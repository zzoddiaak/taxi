package rating_service.rating_service.dto;

import lombok.Data;

@Data
public class RatingEvent {
    private Long userId;
    private Float rating;
    private String userType;
}