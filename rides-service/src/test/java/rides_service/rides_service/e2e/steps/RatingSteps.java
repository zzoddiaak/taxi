package rides_service.rides_service.e2e.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import rides_service.rides_service.e2e.context.TestContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RatingSteps {

    @Autowired
    private TestContext context;

    @Autowired
    @Qualifier("passengerWireMock")
    private WireMockServer passengerWireMock;

    @Autowired
    @Qualifier("driverWireMock")
    private WireMockServer driverWireMock;

    @Autowired
    @Qualifier("ratingWireMock")
    private WireMockServer ratingWireMock;

    @Autowired
    @Qualifier("rideWireMock")
    private WireMockServer rideWireMock;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @When("driver {string} rates passenger {string} with score {string}")
    public void driverRatesPassengerWithScore(String driverId, String passengerId, String score) {
        Objects.requireNonNull(context.getDriverServiceUrl(), "Driver service URL must be set");

        System.out.println("Attempting to rate passenger. DriverService URL: " + context.getDriverServiceUrl());

        given()
                .baseUri(context.getDriverServiceUrl())
                .contentType("application/json")
                .pathParam("driverId", driverId)
                .pathParam("passengerId", passengerId)
                .queryParam("rating", score)
                .when()
                .post("/api/v1/drivers/{driverId}/rate-passenger/{passengerId}")
                .then()
                .log().all()
                .statusCode(200);
    }
    @When("passenger {string} rates driver {string} with score {string}")
    public void passengerRatesDriver(String passengerId, String driverId, String score) {
        rideWireMock.stubFor(
                post(urlPathEqualTo("/api/rides/" + context.getRideId() + "/rating"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{\"status\":\"RATED\"}")));

        given()
                .baseUri(context.getRideServiceUrl())
                .contentType("application/json")
                .pathParam("rideId", context.getRideId())
                .body(String.format(
                        "{\"passengerId\":\"%s\",\"driverId\":\"%s\",\"score\":%s}",
                        passengerId, driverId, score))
                .when()
                .post("/api/rides/{rideId}/rating")
                .then()
                .statusCode(200);
    }

    @When("driver {string} rates passenger {string} with score {double}")
    public void driverRatesPassenger(String driverId, String passengerId, Double rating) throws Exception {
        context.setDriverId(driverId);
        context.setPassengerId(passengerId);

        driverWireMock.stubFor(
                post(urlPathEqualTo("/api/v1/drivers/" + driverId + "/rate-passenger/" + passengerId))
                        .withQueryParam("rating", equalTo(rating.toString()))
                        .willReturn(aResponse().withStatus(200)));

        given()
                .baseUri(context.getDriverServiceUrl())
                .contentType("application/json")
                .pathParam("driverId", driverId)
                .pathParam("passengerId", passengerId)
                .param("rating", rating)
                .when()
                .post("/api/v1/drivers/{driverId}/rate-passenger/{passengerId}")
                .then()
                .statusCode(200);

        kafkaTemplate.send("driver-rating-topic",
                String.format("{\"driverId\":\"%s\",\"passengerId\":\"%s\",\"rating\":%s}",
                        driverId, passengerId, rating));
    }

    @Then("a rating should be created in the rating service")
    public void verifyRatingCreated() throws Exception {
        Map<String, Object> rating = new HashMap<>();
        rating.put("id", 1);
        rating.put("driverId", context.getDriverId());
        rating.put("passengerId", context.getPassengerId());
        rating.put("rating", 5.0);

        Map<String, Object> response = new HashMap<>();
        response.put("ratings", Collections.singletonList(rating));

        ratingWireMock.stubFor(
                get(urlPathEqualTo("/api/ratings"))
                        .withQueryParam("driverId", equalTo(context.getDriverId()))
                        .withQueryParam("passengerId", equalTo(context.getPassengerId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(response))));

        given()
                .baseUri(context.getRatingServiceUrl())
                .queryParam("driverId", context.getDriverId())
                .queryParam("passengerId", context.getPassengerId())
                .when()
                .get("/api/ratings")
                .then()
                .statusCode(200)
                .body("ratings.size()", greaterThan(0));
    }

    @When("attempting to create a rating for non-existent passenger {string}")
    public void createRatingForNonExistentPassenger(String passengerId) throws Exception {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Passenger not found");

        ratingWireMock.stubFor(
                post(urlPathEqualTo("/api/ratings"))
                        .willReturn(aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(errorResponse))));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("passengerId", passengerId);
        requestBody.put("driverId", 1);
        requestBody.put("rating", 5.0);

        Response response = given()
                .baseUri(context.getRatingServiceUrl())
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(requestBody))
                .when()
                .post("/api/ratings");

        context.setResponse(response);
    }

    @Then("the request should fail with {string}")
    public void verifyRequestFailed(String status) {
        int expectedStatus = Integer.parseInt(status.split(" ")[0]);
        context.getResponse().then().statusCode(expectedStatus);
    }
}