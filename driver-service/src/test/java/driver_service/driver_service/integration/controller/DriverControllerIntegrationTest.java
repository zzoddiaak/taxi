package driver_service.driver_service.integration.controller;

import driver_service.driver_service.dto.car.CarDto;
import driver_service.driver_service.dto.driver.DriverRequestDto;
import driver_service.driver_service.dto.driver.DriverResponseDto;
import driver_service.driver_service.dto.payment.PaymentStatusUpdateDto;
import driver_service.driver_service.service.api.DriverService;
import driver_service.driver_service.service.api.PaymentServiceClient;
import driver_service.driver_service.service.kafka.KafkaProducerService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"ride-start", "ride-end", "ride-acceptance", "passenger-rating-topic"})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DriverControllerIntegrationTest {

    @MockBean
    private KafkaProducerService kafkaProducerService;


    @MockBean
    private PaymentServiceClient paymentServiceClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void ratePassenger() {
        given()
                .when()
                .post("/api/v1/drivers/{driverId}/rate-passenger/{passengerId}?rating=5.0", 1L, 2L)
                .then()
                .statusCode(200);

        verify(kafkaProducerService, times(1)).ratePassenger(1L, 2L, 5.0f);
    }

    @Test
    void startRide() {
        given()
                .when()
                .post("/api/v1/drivers/{driverId}/start-ride/{rideId}", 1L, 123L)
                .then()
                .statusCode(200);

        verify(kafkaProducerService, times(1)).sendRideStart("123");
    }

    @Test
    void endRide() {
        given()
                .when()
                .post("/api/v1/drivers/{driverId}/end-ride/{rideId}", 1L, 123L)
                .then()
                .statusCode(200);
    }

    @Test
    void updatePaymentStatus() {
        PaymentStatusUpdateDto paymentStatusUpdateDto = new PaymentStatusUpdateDto();
        paymentStatusUpdateDto.setStatus("PAID");

        given()
                .contentType(ContentType.JSON)
                .body(paymentStatusUpdateDto)
                .when()
                .put("/api/v1/drivers/payments/{paymentId}/status", 1L)
                .then()
                .statusCode(200);

        verify(paymentServiceClient, times(1)).updatePaymentStatus(1L, paymentStatusUpdateDto);
    }

    @Test
    void acceptRide() {
        given()
                .when()
                .post("/api/v1/drivers/{driverId}/accept-ride/{rideId}?accepted=true", 1L, 123L)
                .then()
                .statusCode(200);
    }

    @Test
    void createDriver() {
        CarDto carDto = CarDto.builder()
                .model("Toyota")
                .plateNumber("ABC123")
                .build();

        DriverRequestDto driverRequestDto = DriverRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .licenseNumber("LIC123")
                .car(carDto)
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(driverRequestDto)
                .when()
                .post("/api/v1/drivers")
                .then()
                .statusCode(201)
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .body("email", equalTo("john.doe@example.com"))
                .body("phoneNumber", equalTo("1234567890"))
                .body("licenseNumber", equalTo("LIC123"));
    }

    @Test
    void getDriverById() {

        CarDto carDto = CarDto.builder()
                .model("Toyota")
                .plateNumber("ABC123")
                .build();

        DriverRequestDto driverRequestDto = DriverRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .licenseNumber("LIC123")
                .car(carDto)
                .build();

        DriverResponseDto driverResponseDto = given()
                .contentType(ContentType.JSON)
                .body(driverRequestDto)
                .when()
                .post("/api/v1/drivers")
                .then()
                .extract()
                .as(DriverResponseDto.class);

        given()
                .when()
                .get("/api/v1/drivers/{id}", driverResponseDto.getId())
                .then()
                .statusCode(200)
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .body("email", equalTo("john.doe@example.com"))
                .body("phoneNumber", equalTo("1234567890"))
                .body("licenseNumber", equalTo("LIC123"));
    }

    @Test
    void getAllDrivers() {

        CarDto carDto = CarDto.builder()
                .model("Toyota")
                .plateNumber("ABC123")
                .build();

        CarDto carDto1 = CarDto.builder()
                .model("Toyota")
                .plateNumber("ABC223")
                .build();

        DriverRequestDto driver1 = DriverRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .licenseNumber("LIC123")
                .car(carDto)
                .build();

        DriverRequestDto driver2 = DriverRequestDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .phoneNumber("0987654321")
                .licenseNumber("LIC456")
                .car(carDto1)
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(driver1)
                .when()
                .post("/api/v1/drivers")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(driver2)
                .when()
                .post("/api/v1/drivers")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/api/v1/drivers")
                .then()
                .statusCode(200)
                .body("drivers.size()", equalTo(2))
                .body("drivers[0].firstName", equalTo("John"))
                .body("drivers[1].firstName", equalTo("Jane"));
    }
    @Test
    void updateDriver() {

        CarDto carDto = CarDto.builder()
                .model("Toyota")
                .plateNumber("ABC123")
                .build();

        CarDto carDtoUpdate = CarDto.builder()
                .model("Toyota")
                .plateNumber("ABC323")
                .build();

        DriverRequestDto driverRequestDto = DriverRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .licenseNumber("LIC123")
                .car(carDto)
                .build();

        DriverResponseDto driverResponseDto = given()
                .contentType(ContentType.JSON)
                .body(driverRequestDto)
                .when()
                .post("/api/v1/drivers")
                .then()
                .extract()
                .as(DriverResponseDto.class);

        DriverRequestDto updatedDriver = DriverRequestDto.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("john.updated@example.com")
                .phoneNumber("0987654321")
                .licenseNumber("LIC456")
                .car(carDtoUpdate)
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(updatedDriver)
                .when()
                .put("/api/v1/drivers/{id}", driverResponseDto.getId())
                .then()
                .statusCode(200)
                .body("firstName", equalTo("John Updated"))
                .body("lastName", equalTo("Doe Updated"))
                .body("email", equalTo("john.updated@example.com"))
                .body("phoneNumber", equalTo("0987654321"))
                .body("licenseNumber", equalTo("LIC456"));
    }

    @Test
    void deleteDriver() {

        CarDto carDto = CarDto.builder()
                .model("Toyota")
                .plateNumber("ABC123")
                .build();

        DriverRequestDto driverRequestDto = DriverRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .licenseNumber("LIC123")
                .car(carDto)
                .build();

        DriverResponseDto driverResponseDto = given()
                .contentType(ContentType.JSON)
                .body(driverRequestDto)
                .when()
                .post("/api/v1/drivers")
                .then()
                .extract()
                .as(DriverResponseDto.class);

        given()
                .when()
                .delete("/api/v1/drivers/{id}", driverResponseDto.getId())
                .then()
                .statusCode(204);
    }
}