package rating_service.rating_service.integration.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import rating_service.rating_service.dto.RatingRequestDto;
import rating_service.rating_service.dto.RatingResponseDto;
import rating_service.rating_service.entity.Rating;
import rating_service.rating_service.repository.RatingRepository;
import rating_service.rating_service.service.api.DriverServiceClient;
import rating_service.rating_service.service.api.PassengerServiceClient;
import rating_service.rating_service.service.impl.RatingServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(topics = {"passenger-rating-topic", "driver-rating-topic"})
class RatingServiceImplIntegrationTest {

    @Autowired
    private RatingServiceImpl ratingService;

    @Autowired
    private RatingRepository ratingRepository;

    @MockBean
    private DriverServiceClient driverServiceClient;

    @MockBean
    private PassengerServiceClient passengerServiceClient;

    @AfterEach
    void tearDown() {
        ratingRepository.deleteAll();
    }

    @Test
    void createRating_ShouldCreateRating() {
        RatingRequestDto ratingRequestDto = new RatingRequestDto();
        ratingRequestDto.setDriverId(1L);
        ratingRequestDto.setPassengerId(1L);
        ratingRequestDto.setRating(5.0f);
        ratingRequestDto.setComment("Great service!");

        when(driverServiceClient.updateDriverRating(anyLong(), any())).thenReturn(null);
        when(passengerServiceClient.updatePassengerRating(anyLong(), any())).thenReturn(null);

        RatingResponseDto result = ratingService.createRating(ratingRequestDto);

        assertNotNull(result);
        assertEquals(5.0f, result.getRating());
        assertEquals("Great service!", result.getComment());

        List<Rating> ratings = ratingRepository.findAll();
        assertEquals(1, ratings.size());
        assertEquals(5.0f, ratings.get(0).getRating());
    }

    @Test
    void getRatingById_ShouldReturnRating() {
        Rating rating = new Rating();
        rating.setDriverId(1L);
        rating.setPassengerId(1L);
        rating.setRating(5.0f);
        rating.setComment("Great service!");
        rating.setCreatedAt(LocalDateTime.now());
        ratingRepository.save(rating);

        RatingResponseDto result = ratingService.getRatingById(rating.getId());

        assertNotNull(result);
        assertEquals(5.0f, result.getRating());
        assertEquals("Great service!", result.getComment());
    }
}