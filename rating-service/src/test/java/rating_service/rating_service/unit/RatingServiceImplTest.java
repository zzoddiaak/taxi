package rating_service.rating_service.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import rating_service.rating_service.service.impl.RatingServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private DtoMapper mapper;

    @Mock
    private DriverServiceClient driverServiceClient;

    @Mock
    private PassengerServiceClient passengerServiceClient;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Test
    void createRating_ShouldCreateRating_WhenValidRequest() {
        RatingRequestDto ratingRequestDto = new RatingRequestDto();
        ratingRequestDto.setDriverId(1L);
        ratingRequestDto.setPassengerId(1L);
        ratingRequestDto.setRating(5.0f);
        ratingRequestDto.setComment("Great service!");

        Rating rating = new Rating();
        rating.setId(1L);
        rating.setDriverId(1L);
        rating.setPassengerId(1L);
        rating.setRating(5.0f);
        rating.setComment("Great service!");
        rating.setCreatedAt(LocalDateTime.now());

        when(mapper.convertToEntity(ratingRequestDto, Rating.class)).thenReturn(rating);
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(mapper.convertToDto(rating, RatingResponseDto.class)).thenReturn(new RatingResponseDto());

        RatingResponseDto result = ratingService.createRating(ratingRequestDto);

        assertNotNull(result);
        verify(ratingRepository, times(1)).save(rating);
        verify(driverServiceClient, times(1)).updateDriverRating(eq(1L), any(RatingUpdateDto.class));
        verify(passengerServiceClient, times(1)).updatePassengerRating(eq(1L), any(RatingUpdateDto.class));
    }

    @Test
    void getRatingById_ShouldReturnRatingResponseDto_WhenRatingExists() {
        Long ratingId = 1L;
        Rating rating = new Rating();
        rating.setId(ratingId);
        rating.setDriverId(1L);
        rating.setPassengerId(1L);
        rating.setRating(5.0f);
        rating.setComment("Great service!");
        rating.setCreatedAt(LocalDateTime.now());

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
        when(mapper.convertToDto(rating, RatingResponseDto.class)).thenReturn(new RatingResponseDto());

        RatingResponseDto result = ratingService.getRatingById(ratingId);

        assertNotNull(result);
        verify(ratingRepository, times(1)).findById(ratingId);
        verify(mapper, times(1)).convertToDto(rating, RatingResponseDto.class);
    }

    @Test
    void getRatingById_ShouldThrowRatingNotFoundException_WhenRatingNotFound() {
        Long ratingId = 1L;
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.empty());

        assertThrows(RatingNotFoundException.class, () -> ratingService.getRatingById(ratingId));
        verify(ratingRepository, times(1)).findById(ratingId);
    }

    @Test
    void getAllRatings_ShouldReturnListOfRatings() {
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setDriverId(1L);
        rating.setPassengerId(1L);
        rating.setRating(5.0f);
        rating.setComment("Great service!");
        rating.setCreatedAt(LocalDateTime.now());

        List<Rating> ratings = Collections.singletonList(rating);
        when(ratingRepository.findAll()).thenReturn(ratings);
        when(mapper.convertToDto(rating, RatingResponseDto.class)).thenReturn(new RatingResponseDto());

        RatingListResponseDto result = ratingService.getAllRatings();

        assertNotNull(result);
        assertEquals(1, result.getRatings().size());
        verify(ratingRepository, times(1)).findAll();
        verify(mapper, times(1)).convertToDto(rating, RatingResponseDto.class);
    }

    @Test
    void updateRating_ShouldUpdateRating_WhenValidRequest() {
        Long ratingId = 1L;
        RatingRequestDto ratingRequestDto = new RatingRequestDto();
        ratingRequestDto.setDriverId(1L);
        ratingRequestDto.setPassengerId(1L);
        ratingRequestDto.setRating(4.5f);
        ratingRequestDto.setComment("Good service!");

        Rating existingRating = new Rating();
        existingRating.setId(ratingId);
        existingRating.setDriverId(1L);
        existingRating.setPassengerId(1L);
        existingRating.setRating(5.0f);
        existingRating.setComment("Great service!");
        existingRating.setCreatedAt(LocalDateTime.now());

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(existingRating));
        when(ratingRepository.save(existingRating)).thenReturn(existingRating);
        when(mapper.convertToDto(existingRating, RatingResponseDto.class)).thenReturn(new RatingResponseDto());

        RatingResponseDto result = ratingService.updateRating(ratingId, ratingRequestDto);

        assertNotNull(result);
        verify(ratingRepository, times(1)).findById(ratingId);
        verify(ratingRepository, times(1)).save(existingRating);
        verify(mapper, times(1)).convertToDto(existingRating, RatingResponseDto.class);
    }

    @Test
    void deleteRating_ShouldDeleteRating_WhenRatingExists() {
        Long ratingId = 1L;
        Rating rating = new Rating();
        rating.setId(ratingId);

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
        doNothing().when(ratingRepository).delete(rating);

        ratingService.deleteRating(ratingId);

        verify(ratingRepository, times(1)).findById(ratingId);
        verify(ratingRepository, times(1)).delete(rating);
    }
}