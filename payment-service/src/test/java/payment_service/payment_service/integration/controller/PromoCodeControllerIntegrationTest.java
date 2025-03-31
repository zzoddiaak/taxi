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
import payment_service.payment_service.dto.promo.PromoCodeRequestDto;
import payment_service.payment_service.dto.promo.PromoCodeResponseDto;
import payment_service.payment_service.dto.promo.DiscountRequestDto;
import payment_service.payment_service.integration.config.TestContainersInitializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"ride-completed"})
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestContainersInitializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PromoCodeControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createPromoCode_ShouldReturnCreatedPromoCode() {
        PromoCodeRequestDto promoCodeRequestDto = new PromoCodeRequestDto();
        promoCodeRequestDto.setCode("SUMMER2023");
        promoCodeRequestDto.setDiscountPercentage(BigDecimal.valueOf(10));
        promoCodeRequestDto.setExpirationDate(LocalDateTime.now().plusDays(30));

        given()
                .contentType(ContentType.JSON)
                .body(promoCodeRequestDto)
                .when()
                .post("/api/promocodes")
                .then()
                .statusCode(201)
                .body("code", equalTo("SUMMER2023"))
                .body("discountPercentage", equalTo(10))
                .body("expirationDate", notNullValue());
    }

    @Test
    void getPromoCodeById_ShouldReturnPromoCode() {
        PromoCodeRequestDto promoCodeRequestDto = new PromoCodeRequestDto();
        promoCodeRequestDto.setCode("SUMMER2023");
        promoCodeRequestDto.setDiscountPercentage(BigDecimal.valueOf(10));
        promoCodeRequestDto.setExpirationDate(LocalDateTime.now().plusDays(30));

        PromoCodeResponseDto promoCodeResponseDto = given()
                .contentType(ContentType.JSON)
                .body(promoCodeRequestDto)
                .when()
                .post("/api/promocodes")
                .then()
                .extract()
                .as(PromoCodeResponseDto.class);

        given()
                .when()
                .get("/api/promocodes/{id}", promoCodeResponseDto.getId())
                .then()
                .statusCode(200)
                .body("code", equalTo("SUMMER2023"))
                .body("discountPercentage", equalTo(10.0F))
                .body("expirationDate", notNullValue());
    }

    @Test
    void getPromoCodeByCode_ShouldReturnPromoCode() {
        PromoCodeRequestDto promoCodeRequestDto = new PromoCodeRequestDto();
        promoCodeRequestDto.setCode("SUMMER2023");
        promoCodeRequestDto.setDiscountPercentage(BigDecimal.valueOf(10));
        promoCodeRequestDto.setExpirationDate(LocalDateTime.now().plusDays(30));

        given()
                .contentType(ContentType.JSON)
                .body(promoCodeRequestDto)
                .when()
                .post("/api/promocodes")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/api/promocodes/code/{code}", "SUMMER2023")
                .then()
                .statusCode(200)
                .body("code", equalTo("SUMMER2023"))
                .body("discountPercentage", equalTo(10.0F))
                .body("expirationDate", notNullValue());
    }

    @Test
    void applyDiscount_ShouldReturnDiscountedAmount() {
        PromoCodeRequestDto promoCodeRequestDto = new PromoCodeRequestDto();
        promoCodeRequestDto.setCode("SUMMER2023");
        promoCodeRequestDto.setDiscountPercentage(BigDecimal.valueOf(10));
        promoCodeRequestDto.setExpirationDate(LocalDateTime.now().plusDays(30));

        given()
                .contentType(ContentType.JSON)
                .body(promoCodeRequestDto)
                .when()
                .post("/api/promocodes")
                .then()
                .statusCode(201);

        DiscountRequestDto discountRequestDto = new DiscountRequestDto();
        discountRequestDto.setAmount(BigDecimal.valueOf(100));
        discountRequestDto.setPromoCode("SUMMER2023");

        given()
                .contentType(ContentType.JSON)
                .body(discountRequestDto)
                .when()
                .post("/api/promocodes/apply-discount")
                .then()
                .statusCode(200)
                .body(equalTo("90.00"));
    }
}