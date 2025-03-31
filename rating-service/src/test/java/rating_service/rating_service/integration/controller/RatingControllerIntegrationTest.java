package rating_service.rating_service.integration.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import rating_service.rating_service.dto.RatingListResponseDto;
import rating_service.rating_service.dto.RatingRequestDto;
import rating_service.rating_service.dto.RatingResponseDto;
import rating_service.rating_service.service.api.RatingService;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"passenger-rating-topic", "driver-rating-topic"})
@ActiveProfiles("test")
class RatingControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @MockBean
    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createRating_ShouldReturnCreatedRating() {
        RatingRequestDto ratingRequestDto = new RatingRequestDto();
        ratingRequestDto.setDriverId(1L);
        ratingRequestDto.setPassengerId(1L);
        ratingRequestDto.setRating(5.0f);
        ratingRequestDto.setComment("Great service!");

        RatingResponseDto ratingResponseDto = new RatingResponseDto();
        ratingResponseDto.setId(1L);
        ratingResponseDto.setDriverId(1L);
        ratingResponseDto.setPassengerId(1L);
        ratingResponseDto.setRating(5.0f);
        ratingResponseDto.setComment("Great service!");

        when(ratingService.createRating(ratingRequestDto)).thenReturn(ratingResponseDto);

        given()
                .contentType(ContentType.JSON)
                .body(ratingRequestDto)
                .when()
                .post("/api/ratings")
                .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("driverId", equalTo(1))
                .body("passengerId", equalTo(1))
                .body("rating", equalTo(5.0f))
                .body("comment", equalTo("Great service!"));
    }

    @Test
    void getRatingById_ShouldReturnRating() {
        RatingResponseDto ratingResponseDto = new RatingResponseDto();
        ratingResponseDto.setId(1L);
        ratingResponseDto.setDriverId(1L);
        ratingResponseDto.setPassengerId(1L);
        ratingResponseDto.setRating(5.0f);
        ratingResponseDto.setComment("Great service!");

        when(ratingService.getRatingById(1L)).thenReturn(ratingResponseDto);

        given()
                .when()
                .get("/api/ratings/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("driverId", equalTo(1))
                .body("passengerId", equalTo(1))
                .body("rating", equalTo(5.0f))
                .body("comment", equalTo("Great service!"));
    }

    @Test
    void getAllRatings_ShouldReturnListOfRatings() {
        RatingResponseDto ratingResponseDto = new RatingResponseDto();
        ratingResponseDto.setId(1L);
        ratingResponseDto.setDriverId(1L);
        ratingResponseDto.setPassengerId(1L);
        ratingResponseDto.setRating(5.0f);
        ratingResponseDto.setComment("Great service!");

        RatingListResponseDto ratingListResponseDto = new RatingListResponseDto();
        ratingListResponseDto.setRatings(List.of(ratingResponseDto));

        when(ratingService.getAllRatings()).thenReturn(ratingListResponseDto);

        given()
                .when()
                .get("/api/ratings")
                .then()
                .statusCode(200)
                .body("ratings[0].id", equalTo(1))
                .body("ratings[0].driverId", equalTo(1))
                .body("ratings[0].passengerId", equalTo(1))
                .body("ratings[0].rating", equalTo(5.0f))
                .body("ratings[0].comment", equalTo("Great service!"));
    }

    @Test
    void updateRating_ShouldReturnUpdatedRating() {
        RatingRequestDto ratingRequestDto = new RatingRequestDto();
        ratingRequestDto.setDriverId(1L);
        ratingRequestDto.setPassengerId(1L);
        ratingRequestDto.setRating(4.5f);
        ratingRequestDto.setComment("Good service!");

        RatingResponseDto ratingResponseDto = new RatingResponseDto();
        ratingResponseDto.setId(1L);
        ratingResponseDto.setDriverId(1L);
        ratingResponseDto.setPassengerId(1L);
        ratingResponseDto.setRating(4.5f);
        ratingResponseDto.setComment("Good service!");

        when(ratingService.updateRating(1L, ratingRequestDto)).thenReturn(ratingResponseDto);

        given()
                .contentType(ContentType.JSON)
                .body(ratingRequestDto)
                .when()
                .put("/api/ratings/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("driverId", equalTo(1))
                .body("passengerId", equalTo(1))
                .body("rating", equalTo(4.5f))
                .body("comment", equalTo("Good service!"));
    }

    @Test
    void deleteRating_ShouldReturnNoContent() {
        doNothing().when(ratingService).deleteRating(1L);

        given()
                .when()
                .delete("/api/ratings/1")
                .then()
                .statusCode(204);
    }
}
