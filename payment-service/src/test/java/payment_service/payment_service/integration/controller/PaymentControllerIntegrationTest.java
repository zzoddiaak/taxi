package payment_service.payment_service.integration.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import payment_service.payment_service.dto.payment.PaymentRequestDto;
import payment_service.payment_service.dto.payment.PaymentResponseDto;
import payment_service.payment_service.dto.payment.PaymentStatusUpdateDto;
import payment_service.payment_service.integration.config.TestContainersInitializer;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestContainersInitializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PaymentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createPayment_ShouldReturnCreatedPayment() {
        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .rideId(1L)
                .passengerId(1L)
                .paymentMethod("CARD")
                .amount(BigDecimal.valueOf(100.1))
                .status("pending")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(paymentRequestDto)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(201)
                .body("rideId", equalTo(1))
                .body("amount", equalTo(100.1F))
                .body("paymentMethod", equalTo("CARD"))
                .body("status", equalTo("pending"));
    }

    @Test
    void getPaymentById_ShouldReturnPayment() {
        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .rideId(1L)
                .passengerId(1L)
                .paymentMethod("CARD")
                .amount(BigDecimal.valueOf(100.1))
                .status("pending")
                .build();

        PaymentResponseDto paymentResponseDto = given()
                .contentType(ContentType.JSON)
                .body(paymentRequestDto)
                .when()
                .post("/api/payments")
                .then()
                .extract()
                .as(PaymentResponseDto.class);

        given()
                .when()
                .get("/api/payments/{id}", paymentResponseDto.getId())
                .then()
                .statusCode(200)
                .body("rideId", equalTo(1))
                .body("amount", equalTo(100.1F))
                .body("paymentMethod", equalTo("CARD"))
                .body("status", equalTo("pending"));
    }



    @Test
    void getPaymentByRideId_ShouldReturnPayment() {
        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .rideId(1L)
                .passengerId(1L)
                .paymentMethod("CARD")
                .amount(BigDecimal.valueOf(100.1))
                .status("pending")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(paymentRequestDto)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/api/payments/ride/{rideId}", 1L)
                .then()
                .statusCode(200)
                .body("rideId", equalTo(1))
                .body("amount", equalTo(100.1F))
                .body("paymentMethod", equalTo("CARD"))
                .body("status", equalTo("pending"));
    }

    @Test
    void getAllPayments_ShouldReturnListOfPayments() {
        PaymentRequestDto payment1 = new PaymentRequestDto();
        payment1.setRideId(1L);
        payment1.setPassengerId(1L);
        payment1.setAmount(BigDecimal.valueOf(100));
        payment1.setPaymentMethod("CARD");
        payment1.setStatus("pending");

        PaymentRequestDto payment2 = new PaymentRequestDto();
        payment2.setRideId(2L);
        payment2.setPassengerId(2L);
        payment2.setAmount(BigDecimal.valueOf(200));
        payment2.setPaymentMethod("CASH");
        payment2.setStatus("completed");

        given().contentType(ContentType.JSON).body(payment1).when().post("/api/payments");
        given().contentType(ContentType.JSON).body(payment2).when().post("/api/payments");

        given()
                .when()
                .get("/api/payments")
                .then()
                .statusCode(200)
                .body("payments.size()", equalTo(2))
                .body("payments[0].rideId", equalTo(1))
                .body("payments[1].rideId", equalTo(2));
    }

    @Test
    void updatePayment_ShouldReturnUpdatedPayment() {
        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .rideId(1L)
                .passengerId(1L)
                .paymentMethod("CARD")
                .amount(BigDecimal.valueOf(100.1))
                .status("pending")
                .build();;

        PaymentResponseDto paymentResponseDto = given()
                .contentType(ContentType.JSON)
                .body(paymentRequestDto)
                .when()
                .post("/api/payments")
                .then()
                .extract()
                .as(PaymentResponseDto.class);

        PaymentRequestDto updatedPayment = PaymentRequestDto.builder()
                .rideId(2L)
                .passengerId(2L)
                .paymentMethod("CASH")
                .amount(BigDecimal.valueOf(200.1))
                .status("completed")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(updatedPayment)
                .when()
                .put("/api/payments/{id}", paymentResponseDto.getId())
                .then()
                .statusCode(200)
                .body("rideId", equalTo(2))
                .body("amount", equalTo(200.1F))
                .body("paymentMethod", equalTo("CASH"))
                .body("status", equalTo("completed"));
    }

    @Test
    void deletePayment_ShouldReturnNoContent() {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setRideId(1L);
        paymentRequestDto.setPassengerId(1L);
        paymentRequestDto.setAmount(BigDecimal.valueOf(100));
        paymentRequestDto.setPaymentMethod("CARD");
        paymentRequestDto.setStatus("pending");

        PaymentResponseDto paymentResponseDto = given()
                .contentType(ContentType.JSON)
                .body(paymentRequestDto)
                .when()
                .post("/api/payments")
                .then()
                .extract()
                .as(PaymentResponseDto.class);

        given()
                .when()
                .delete("/api/payments/{id}", paymentResponseDto.getId())
                .then()
                .statusCode(204);
    }
}