package rating_service.rating_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("Creating new rating for driver={}, passenger={}",
                ratingRequestDto.getDriverId(), ratingRequestDto.getPassengerId());

        Rating rating = mapper.convertToEntity(ratingRequestDto, Rating.class);
        rating.setCreatedAt(LocalDateTime.now());
        Rating savedRating = ratingRepository.save(rating);
        log.debug("Rating created: {}", savedRating);

        if (ratingRequestDto.getDriverId() != null) {
            log.info("Updating driver rating: driverId={}", ratingRequestDto.getDriverId());
            driverServiceClient.updateDriverRating(
                    ratingRequestDto.getDriverId(),
                    new RatingUpdateDto(ratingRequestDto.getRating())
            );
        }

        if (ratingRequestDto.getPassengerId() != null) {
            log.info("Updating passenger rating: passengerId={}", ratingRequestDto.getPassengerId());
            passengerServiceClient.updatePassengerRating(
                    ratingRequestDto.getPassengerId(),
                    new RatingUpdateDto(ratingRequestDto.getRating())
            );
        }

        log.info("Rating created successfully: ratingId={}", savedRating.getId());
        return mapper.convertToDto(savedRating, RatingResponseDto.class);
    }

    @Override
    public RatingResponseDto getRatingById(Long id) {
        log.info("Fetching rating by id: {}", id);
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Rating not found: id={}", id);
                    return new RatingNotFoundException("Rating not found with id: " + id);
                });
        log.debug("Found rating: {}", rating);
        return mapper.convertToDto(rating, RatingResponseDto.class);
    }

    @Override
    public RatingListResponseDto getAllRatings() {
        log.info("Fetching all ratings");
        List<Rating> ratings = ratingRepository.findAll();
        log.info("Found {} ratings", ratings.size());

        List<RatingResponseDto> ratingResponseDto = ratings.stream()
                .map(payment -> mapper.convertToDto(payment, RatingResponseDto.class))
                .toList();

        return RatingListResponseDto.builder()
                .ratings(ratingResponseDto)
                .build();
    }

    @Override
    public RatingResponseDto updateRating(Long id, RatingRequestDto ratingRequestDto) {
        log.info("Updating rating: id={}, newData={}", id, ratingRequestDto);
        Rating existingRating = ratingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Rating not found during update: id={}", id);
                    return new RatingNotFoundException("Rating not found with id: " + id);
                });

        existingRating.setDriverId(ratingRequestDto.getDriverId());
        existingRating.setPassengerId(ratingRequestDto.getPassengerId());
        existingRating.setRating(ratingRequestDto.getRating());
        existingRating.setComment(ratingRequestDto.getComment());

        Rating updatedRating = ratingRepository.save(existingRating);
        log.info("Rating updated successfully: {}", updatedRating);
        return mapper.convertToDto(updatedRating, RatingResponseDto.class);
    }

    @Override
    public void deleteRating(Long id) {
        log.info("Deleting rating: id={}", id);
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Rating not found during deletion: id={}", id);
                    return new RatingNotFoundException("Rating not found with id: " + id);
                });
        ratingRepository.delete(rating);
        log.info("Rating deleted: id={}", id);
    }
}