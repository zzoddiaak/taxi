package rating_service.rating_service.service.api;

import rating_service.rating_service.dto.RatingRequestDto;
import rating_service.rating_service.dto.RatingResponseDto;

import java.util.List;

public interface RatingService {
    RatingResponseDto createRating(RatingRequestDto ratingRequestDto);
    RatingResponseDto getRatingById(Long id);
    List<RatingResponseDto> getAllRatings();
    RatingResponseDto updateRating(Long id, RatingRequestDto ratingRequestDto);
    void deleteRating(Long id);
}