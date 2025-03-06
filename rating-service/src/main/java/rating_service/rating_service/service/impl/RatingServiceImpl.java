package rating_service.rating_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rating_service.rating_service.config.mapper.DtoMapper;
import rating_service.rating_service.dto.RatingListResponseDto;
import rating_service.rating_service.dto.RatingRequestDto;
import rating_service.rating_service.dto.RatingResponseDto;
import rating_service.rating_service.dto.RatingUpdateDto;
import rating_service.rating_service.entity.Rating;
import rating_service.rating_service.exception.rating.RatingNotFoundException;
import rating_service.rating_service.repository.RatingRepository;
import rating_service.rating_service.service.api.DriverServiceClient;
import rating_service.rating_service.service.api.PassengerServiceClient;
import rating_service.rating_service.service.api.RatingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DtoMapper mapper;
    private final DriverServiceClient driverServiceClient;
    private final PassengerServiceClient passengerServiceClient;

    @Override
    public RatingResponseDto createRating(RatingRequestDto ratingRequestDto) {
        Rating rating = mapper.convertToEntity(ratingRequestDto, Rating.class);
        rating.setCreatedAt(LocalDateTime.now());
        Rating savedRating = ratingRepository.save(rating);

        driverServiceClient.updateDriverRating(
                ratingRequestDto.getDriverId(),
                new RatingUpdateDto(ratingRequestDto.getRating())
        );

        passengerServiceClient.updatePassengerRating(
                ratingRequestDto.getPassengerId(),
                new RatingUpdateDto(ratingRequestDto.getRating())
        );

        return mapper.convertToDto(savedRating, RatingResponseDto.class);
    }

    @Override
    public RatingResponseDto getRatingById(Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RatingNotFoundException(String.format("Rating not found with id: " + id)));
        return mapper.convertToDto(rating, RatingResponseDto.class);
    }

    @Override
    public RatingListResponseDto getAllRatings() {
        List<Rating> ratings = ratingRepository.findAll();
        List<RatingResponseDto> ratingResponseDto = ratings.stream()
                .map(payment -> mapper.convertToDto(payment, RatingResponseDto.class))
                .toList();

        return RatingListResponseDto.builder()
                .ratings(ratingResponseDto)
                .build();
    }

    @Override
    public RatingResponseDto updateRating(Long id, RatingRequestDto ratingRequestDto) {
        Rating existingRating = ratingRepository.findById(id)
                .orElseThrow(() -> new RatingNotFoundException(String.format("Rating not found with id: " + id)));

        existingRating.setDriverId(ratingRequestDto.getDriverId());
        existingRating.setPassengerId(ratingRequestDto.getPassengerId());
        existingRating.setRating(ratingRequestDto.getRating());
        existingRating.setComment(ratingRequestDto.getComment());

        Rating updatedRating = ratingRepository.save(existingRating);
        return mapper.convertToDto(updatedRating, RatingResponseDto.class);
    }

    @Override
    public void deleteRating(Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RatingNotFoundException(String.format("Rating not found with id: " + id)));
        ratingRepository.delete(rating);
    }
}