package passenger_service.passenger_service.integration.controller;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import passenger_service.passenger_service.dto.financial.BalanceUpdateDto;
import passenger_service.passenger_service.dto.passenger.PassengerRequestDto;
import passenger_service.passenger_service.dto.passenger.PassengerResponseDto;
import passenger_service.passenger_service.dto.rating.RatingUpdateDto;
import passenger_service.passenger_service.entity.FinancialData;
import passenger_service.passenger_service.entity.Passenger;
import passenger_service.passenger_service.repository.FinancialDataRepository;
import passenger_service.passenger_service.repository.PassengerRepository;
import passenger_service.passenger_service.service.kafka.KafkaProducerService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"passenger-rating-topic"})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PassengerControllerIntegrationTest {

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FinancialDataRepository financialDataRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void rateDriver() {
        given()
                .when()
                .post("/api/passengers/{passengerId}/rate-driver/{driverId}?rating=5.0", 1L, 2L)
                .then()
                .statusCode(200);

        verify(kafkaProducerService, times(1)).rateDriver(1L, 2L, 5.0f);
    }

    @Test
    void updatePassengerBalance_ShouldReturnOk() {
        Passenger passenger = new Passenger();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPhoneNumber("1234567890");
        passengerRepository.save(passenger);

        FinancialData financialData = new FinancialData();
        financialData.setPassenger(passenger);
        financialData.setBalance(100.0);
        financialDataRepository.save(financialData);

        BalanceUpdateDto balanceUpdateDto = new BalanceUpdateDto();
        balanceUpdateDto.setAmount(50.0);

        given()
                .contentType(ContentType.JSON)
                .body(balanceUpdateDto)
                .when()
                .put("/api/passengers/{id}/balance", passenger.getId())
                .then()
                .statusCode(200);

        FinancialData updatedFinancialData = financialDataRepository.findById(passenger.getId()).orElseThrow();
        assertEquals(50.0, updatedFinancialData.getBalance());
    }

    @Test
    void updatePassengerRating_ShouldReturnOk() {
        Passenger passenger = new Passenger();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPhoneNumber("1234567890");
        passenger.setAverageRating(4.0);
        passenger.setRatingCount(1);
        passengerRepository.save(passenger);

        RatingUpdateDto ratingUpdateDto = new RatingUpdateDto();
        ratingUpdateDto.setRating(5.0f);

        given()
                .contentType(ContentType.JSON)
                .body(ratingUpdateDto)
                .when()
                .put("/api/passengers/{id}/rating", passenger.getId())
                .then()
                .statusCode(200);

        Passenger updatedPassenger = passengerRepository.findById(passenger.getId()).orElseThrow();
        assertEquals(4.5, updatedPassenger.getAverageRating());
        assertEquals(2, updatedPassenger.getRatingCount());
    }

    @Test
    void createPassenger() {


        PassengerRequestDto passengerRequestDto = PassengerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .cardNumber("4111115111111111")
                .balance(100.0)
                .cardCvv("644")
                .cardExpiryDate("12/25")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(passengerRequestDto)
                .when()
                .post("/api/passengers")
                .then()
                .statusCode(201)
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .body("email", equalTo("john.doe@example.com"))
                .body("phoneNumber", equalTo("1234567890"));
    }

 @Test
    void getDriverById() {

        PassengerRequestDto passengerRequestDto = PassengerRequestDto.builder()
             .firstName("John")
             .lastName("Doe")
             .email("john.doe@example.com")
             .phoneNumber("1234567890")
             .cardNumber("4111115111111111")
             .balance(100.0)
             .cardCvv("644")
             .cardExpiryDate("12/25")
             .build();

        PassengerResponseDto passengerResponseDto = given()
                .contentType(ContentType.JSON)
                .body(passengerRequestDto)
                .when()
                .post("/api/passengers")
                .then()
                .extract()
                .as(PassengerResponseDto.class);

        given()
                .when()
                .get("/api/passengers/{id}", passengerResponseDto.getId())
                .then()
                .statusCode(200)
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .body("email", equalTo("john.doe@example.com"))
                .body("phoneNumber", equalTo("1234567890"));
    }

    @Test
    void getAllDrivers() {

        PassengerRequestDto passengerRequestDto = PassengerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .cardNumber("4111115111111111")
                .balance(100.0)
                .cardCvv("644")
                .cardExpiryDate("12/25")
                .build();

        PassengerRequestDto passengerRequestDto1 = PassengerRequestDto.builder()
                .firstName("Joohn")
                .lastName("Doee")
                .email("john.doee@example.com")
                .phoneNumber("1534567890")
                .cardNumber("4141115111111111")
                .balance(100.0)
                .cardCvv("654")
                .cardExpiryDate("12/25")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(passengerRequestDto)
                .when()
                .post("/api/passengers")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(passengerRequestDto1)
                .when()
                .post("/api/passengers")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/api/passengers")
                .then()
                .statusCode(200)
                .body("passengers.size()", equalTo(2))
                .body("passengers[0].firstName", equalTo("John"))
                .body("passengers[1].firstName", equalTo("Joohn"));
    }

    @Test
    void updateDriver() {

        PassengerRequestDto passengerRequestDto = PassengerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .cardNumber("4111115111111111")
                .balance(100.0)
                .cardCvv("644")
                .cardExpiryDate("12/25")
                .build();

        PassengerRequestDto passengerRequestDto1 = PassengerRequestDto.builder()
                .firstName("Joohn")
                .lastName("Doee")
                .email("john.doee@example.com")
                .phoneNumber("1534567890")
                .cardNumber("4141115111111111")
                .balance(100.0)
                .cardCvv("654")
                .cardExpiryDate("12/25")
                .build();

        PassengerResponseDto passengerResponseDto = given()
                .contentType(ContentType.JSON)
                .body(passengerRequestDto)
                .when()
                .post("/api/passengers")
                .then()
                .extract()
                .as(PassengerResponseDto.class);



        given()
                .contentType(ContentType.JSON)
                .body(passengerRequestDto1)
                .when()
                .put("/api/passengers/{id}", passengerResponseDto.getId())
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Joohn"))
                .body("lastName", equalTo("Doee"))
                .body("email", equalTo("john.doee@example.com"))
                .body("phoneNumber", equalTo("1534567890"));
    }

    @Test
    void deleteDriver() {


        PassengerRequestDto passengerRequestDto = PassengerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .cardNumber("4111115111111111")
                .balance(100.0)
                .cardCvv("644")
                .cardExpiryDate("12/25")
                .build();

        PassengerResponseDto passengerResponseDto = given()
                .contentType(ContentType.JSON)
                .body(passengerRequestDto)
                .when()
                .post("/api/passengers")
                .then()
                .extract()
                .as(PassengerResponseDto.class);

        given()
                .when()
                .delete("/api/passengers/{id}", passengerResponseDto.getId())
                .then()
                .statusCode(204);
    }
}