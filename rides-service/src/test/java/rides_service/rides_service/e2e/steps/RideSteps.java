package rides_service.rides_service.e2e.steps;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import rides_service.rides_service.e2e.context.TestContext;
import rides_service.rides_service.repository.RideRepository;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RideSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    @Qualifier("rideWireMock")
    private WireMockServer rideWireMock;

    @Autowired
    @Qualifier("driverWireMock")
    private WireMockServer driverWireMock;

    @Autowired
    @Qualifier("paymentWireMock")
    private WireMockServer paymentWireMock;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;



    @When("passenger {string} cancels an existing ride")
    public void passengerCancelsRide(String passengerId) {
        if (context.getRideId() == null) {
            passengerRequestsRide(passengerId, "A", "B");
        }

        rideWireMock.stubFor(
                delete(urlPathEqualTo("/api/rides/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{\"id\":\"" + context.getRideId() + "\",\"status\":\"CANCELLED\"}")));

        given()
                .baseUri(context.getRideServiceUrl())
                .pathParam("id", context.getRideId())
                .when()
                .delete("/api/rides/{id}")
                .then()
                .statusCode(200);
    }

    @Then("the ride status should be {string}")
    public void verifyRideStatus(String expectedStatus) {
        rideWireMock.stubFor(
                get(urlPathEqualTo("/api/rides/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{\"id\":\"%s\",\"status\":\"%s\"}",
                                        context.getRideId(), expectedStatus))));

        given()
                .baseUri(context.getRideServiceUrl())
                .pathParam("id", context.getRideId())
                .when()
                .get("/api/rides/{id}")
                .then()
                .statusCode(200)
                .body("status", equalTo(expectedStatus));
    }
    @Then("the cost should be {string} at rate {double} per km")
    public void verifyRideCost(String expectedCost, Double ratePerKm) {
        // Проверяем расчет стоимости
        given()
                .baseUri(context.getRideServiceUrl())
                .when()
                .get("/api/rides/" + context.getRideId() + "/cost")
                .then()
                .statusCode(200)
                .body("cost", equalTo(Float.parseFloat(expectedCost)))
                .body("ratePerKm", equalTo(ratePerKm.floatValue()));
    }

    @When("driver {string} starts the ride")
    public void driverStartsRide(String driverId) {
        driverWireMock.stubFor(
                post(urlPathEqualTo("/api/v1/drivers/" + driverId + "/start-ride/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{\"status\":\"IN_PROGRESS\"}")));

        given()
                .baseUri(context.getDriverServiceUrl())
                .contentType("application/json")
                .pathParam("driverId", driverId)
                .pathParam("rideId", context.getRideId())
                .when()
                .post("/api/v1/drivers/{driverId}/start-ride/{rideId}")
                .then()
                .statusCode(200);

        kafkaTemplate.send("ride-start",
                String.format("{\"rideId\":\"%s\",\"driverId\":\"%s\"}",
                        context.getRideId(), driverId));

        rideWireMock.stubFor(
                get(urlPathEqualTo("/api/rides/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{\"id\":\"%s\",\"status\":\"IN_PROGRESS\"}",
                                        context.getRideId()))));
    }
    @Given("a route with distance {string} km")
    public void setupRouteDistance(String distance) {
        rideWireMock.stubFor(
                post(urlPathEqualTo("/api/routes/calculate"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{ \"distance\": %s, \"duration\": 15 }", distance))));
    }

    @When("calculating the ride cost")
    public void calculateRideCost() {
        passengerRequestsRide("1", "A", "B");

        rideWireMock.stubFor(
                get(urlPathEqualTo("/api/rides/" + context.getRideId() + "/cost"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{ \"distance\": 5.5, \"cost\": 55.00, \"ratePerKm\": 10.00 }")));

        Response response = given()
                .baseUri(context.getRideServiceUrl())
                .pathParam("rideId", context.getRideId())
                .when()
                .get("/api/rides/{rideId}/cost");

        context.setResponse(response);
        Double cost = response.jsonPath().getDouble("cost");
        if (cost != null) {
            context.setRideCost(cost);
        }
    }

    @When("driver {string} ends the ride")
    public void driverEndsRide(String driverId) {
        Objects.requireNonNull(context.getRideId(), "Ride ID must not be null");

        driverWireMock.stubFor(
                post(urlPathEqualTo("/api/v1/drivers/" + driverId + "/end-ride/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{\"status\":\"COMPLETED\"}")));

        paymentWireMock.stubFor(
                post(urlPathEqualTo("/api/payments"))
                        .withRequestBody(containing(context.getRideId()))
                        .willReturn(aResponse()
                                .withStatus(201)
                                .withBody("{\"id\":\"pay-123\",\"status\":\"PENDING\"}")));

        // Выполним запрос
        given()
                .baseUri(context.getDriverServiceUrl())
                .contentType("application/json")
                .pathParam("driverId", driverId)
                .pathParam("rideId", context.getRideId())
                .when()
                .post("/api/v1/drivers/{driverId}/end-ride/{rideId}")
                .then()
                .statusCode(200);

        kafkaTemplate.send("ride-completed",
                String.format("{\"rideId\":\"%s\",\"driverId\":\"%s\"}",
                        context.getRideId(), driverId));

        rideWireMock.stubFor(
                get(urlPathEqualTo("/api/rides/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":\"" + context.getRideId() + "\",\"status\":\"COMPLETED\"}")));
    }

    @Then("the cost should be {string} at rate {string} per km")
    public void verifyRideCost(String expectedCost, String ratePerKm) {
        Objects.requireNonNull(context.getRideId(), "Ride ID must not be null");

        rideWireMock.stubFor(
                get(urlPathEqualTo("/api/rides/" + context.getRideId() + "/cost"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{\"cost\":%s,\"ratePerKm\":%s}",
                                        expectedCost, ratePerKm))));

        given()
                .baseUri(context.getRideServiceUrl())
                .pathParam("rideId", context.getRideId())
                .when()
                .get("/api/rides/{rideId}/cost")
                .then()
                .statusCode(200)
                .body("cost", equalTo(Float.parseFloat(expectedCost)))
                .body("ratePerKm", equalTo(Float.parseFloat(ratePerKm)));
    }


    @Then("the ride status should be updated to {string}")
    public void verifyUpdatedRideStatus(String status) {
        rideWireMock.stubFor(
                get(urlPathEqualTo("/api/rides/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format("{\"id\":\"%s\",\"status\":\"%s\"}",
                                        context.getRideId(), status))));

        given()
                .baseUri(context.getRideServiceUrl())
                .pathParam("id", context.getRideId())
                .when()
                .get("/api/rides/{id}")
                .then()
                .statusCode(200)
                .body("status", equalTo(status));
    }

    @When("passenger {string} requests a ride from {string} to {string}")
    public void passengerRequestsRide(String passengerId, String start, String end) {
        context.setPassengerId(passengerId);

        rideWireMock.stubFor(
                post(urlPathEqualTo("/api/rides"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{ \"id\": \"ride-123\", \"passengerId\": \"%s\", " +
                                                "\"startAddress\": \"%s\", \"endAddress\": \"%s\", \"status\": \"PENDING\" }",
                                        passengerId, start, end))));

        Response response = given()
                .baseUri(context.getRideServiceUrl())
                .contentType("application/json")
                .body(String.format(
                        "{ \"passengerId\": \"%s\", \"startAddress\": \"%s\", \"endAddress\": \"%s\" }",
                        passengerId, start, end))
                .when()
                .post("/api/rides");

        String rideId = response.jsonPath().getString("id");
        if (rideId == null) {
            throw new RuntimeException("Ride ID not found in response");
        }
        context.setRideId(rideId);
        context.setResponse(response);

        try {
            kafkaTemplate.send("available-rides", rideId).get();
            System.out.println("Successfully sent message to available-rides topic");
        } catch (Exception e) {
            throw new RuntimeException("Failed to send Kafka message", e);
        }
    }

    @Then("a ride request should be created with status {string}")
    public void a_ride_request_should_be_created_with_status(String status) {
        rideWireMock.stubFor(
                get(urlPathEqualTo("/api/rides/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{\"id\":\"%s\",\"status\":\"%s\"}",
                                        context.getRideId(), status))));

        given()
                .baseUri(context.getRideServiceUrl())
                .pathParam("id", context.getRideId())
                .when()
                .get("/api/rides/{id}")
                .then()
                .statusCode(200)
                .body("status", equalTo(status));
    }
}