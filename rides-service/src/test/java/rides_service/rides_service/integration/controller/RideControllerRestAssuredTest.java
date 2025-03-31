package rides_service.rides_service.integration.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import rides_service.rides_service.dto.ride.RideRequestDto;
import rides_service.rides_service.dto.ride.RideResponseDto;
import rides_service.rides_service.dto.ride.RideListResponseDto;
import rides_service.rides_service.integration.config.TestContainersInitializer;
import rides_service.rides_service.service.api.RideService;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"available-rides", "ride-acceptance", "ride-start", "ride-end"})
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestContainersInitializer.class)
class RideControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    @MockBean
    private RideService rideService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createRide_ShouldReturnCreatedRide() {
        RideRequestDto rideRequestDto = new RideRequestDto();
        rideRequestDto.setDriverId(1L);
        rideRequestDto.setPassengerId(1L);
        rideRequestDto.setRouteId(1L);
        rideRequestDto.setPaymentMethod("CARD");

        RideResponseDto rideResponseDto = new RideResponseDto();
        rideResponseDto.setId(1L);
        rideResponseDto.setStatus("PENDING");

        when(rideService.createRide(rideRequestDto)).thenReturn(rideResponseDto);

        given()
                .contentType(ContentType.JSON)
                .body(rideRequestDto)
                .when()
                .post("/api/rides")
                .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("status", equalTo("PENDING"));
    }

    @Test
    void getRideById_ShouldReturnRide() {
        RideResponseDto rideResponseDto = new RideResponseDto();
        rideResponseDto.setId(1L);
        rideResponseDto.setStatus("PENDING");

        when(rideService.getRideById(1L)).thenReturn(rideResponseDto);

        given()
                .when()
                .get("/api/rides/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("status", equalTo("PENDING"));
    }

    @Test
    void getAllRides_ShouldReturnListOfRides() {
        RideResponseDto rideResponseDto = new RideResponseDto();
        rideResponseDto.setId(1L);
        rideResponseDto.setStatus("PENDING");

        RideListResponseDto rideListResponseDto = new RideListResponseDto();
        rideListResponseDto.setRide(Collections.singletonList(rideResponseDto));

        when(rideService.getAllRides()).thenReturn(rideListResponseDto);

        given()
                .when()
                .get("/api/rides")
                .then()
                .statusCode(200)
                .body("ride[0].id", equalTo(1))
                .body("ride[0].status", equalTo("PENDING"));
    }

    @Test
    void updateRide_ShouldReturnUpdatedRide() {
        RideRequestDto rideRequestDto = new RideRequestDto();
        rideRequestDto.setDriverId(1L);
        rideRequestDto.setPassengerId(1L);
        rideRequestDto.setRouteId(1L);
        rideRequestDto.setPaymentMethod("CARD");

        RideResponseDto rideResponseDto = new RideResponseDto();
        rideResponseDto.setId(1L);
        rideResponseDto.setStatus("ACCEPTED");

        when(rideService.updateRide(1L, rideRequestDto)).thenReturn(rideResponseDto);

        given()
                .contentType(ContentType.JSON)
                .body(rideRequestDto)
                .when()
                .put("/api/rides/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("status", equalTo("ACCEPTED"));
    }

    @Test
    void deleteRide_ShouldReturnNoContent() {
        doNothing().when(rideService).deleteRide(1L);

        given()
                .when()
                .delete("/api/rides/1")
                .then()
                .statusCode(204);
    }
}