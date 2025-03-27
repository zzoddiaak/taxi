package rides_service.rides_service.e2e.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import rides_service.rides_service.e2e.context.TestContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.closeTo;

public class DriverSteps {

    @Autowired
    private TestContext context;

    @Autowired
    @Qualifier("rideWireMock")
    private WireMockServer rideWireMock;

    @Autowired
    @Qualifier("driverWireMock")
    private WireMockServer driverWireMock;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("the following drivers exist:")
    public void createDrivers(io.cucumber.datatable.DataTable dataTable) throws Exception {
        dataTable.asMaps().forEach(driver -> {
            String driverId = driver.get("id");

            try {
                Map<String, Object> driverResponse = new HashMap<>();
                driverResponse.put("id", driverId);
                driverResponse.put("firstName", driver.get("firstName"));
                driverResponse.put("lastName", driver.get("lastName"));

                Map<String, Object> car = new HashMap<>();
                car.put("model", driver.get("carModel"));
                car.put("plateNumber", driver.get("licenseNumber"));
                driverResponse.put("car", car);

                Map<String, Object> rating = new HashMap<>();
                rating.put("averageRating", 4.5);
                rating.put("ratingCount", 10);
                driverResponse.put("rating", rating);

                driverWireMock.stubFor(
                        get(urlPathEqualTo("/api/v1/drivers/" + driverId))
                                .willReturn(aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(objectMapper.writeValueAsString(driverResponse))));

                driverWireMock.stubFor(
                        post(urlPathEqualTo("/api/v1/drivers/" + driverId + "/accept-ride/" + context.getRideId()))
                                .willReturn(aResponse().withStatus(200)));

                driverWireMock.stubFor(
                        post(urlPathEqualTo("/api/v1/drivers/" + driverId + "/start-ride/" + context.getRideId()))
                                .willReturn(aResponse().withStatus(200)));

                driverWireMock.stubFor(
                        post(urlPathEqualTo("/api/v1/drivers/" + driverId + "/end-ride/" + context.getRideId()))
                                .willReturn(aResponse().withStatus(200)));
            } catch (Exception e) {
                throw new RuntimeException("Failed to create driver stub", e);
            }
        });
    }

    @When("driver {string} accepts the ride")
    public void driverAcceptsRide(String driverId) {
        Objects.requireNonNull(context.getRideId(), "Ride ID must not be null");
        context.setDriverId(driverId);

        driverWireMock.stubFor(
                post(urlPathEqualTo("/api/v1/drivers/" + driverId + "/accept-ride/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{\"status\":\"ACCEPTED\"}")));

        rideWireMock.stubFor(
                put(urlPathEqualTo("/api/rides/" + context.getRideId() + "/status"))
                        .withRequestBody(equalToJson("{\"status\":\"ACCEPTED\"}"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{\"id\":\"" + context.getRideId() + "\",\"status\":\"ACCEPTED\"}")));

        given()
                .baseUri(context.getDriverServiceUrl())
                .contentType("application/json")
                .pathParam("driverId", driverId)
                .pathParam("rideId", context.getRideId())
                .when()
                .post("/api/v1/drivers/{driverId}/accept-ride/{rideId}")
                .then()
                .statusCode(200);

        rideWireMock.stubFor(
                get(urlPathEqualTo("/api/rides/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":\"" + context.getRideId() + "\",\"status\":\"ACCEPTED\"}")));

        kafkaTemplate.send("ride-acceptance",
                String.format("{\"rideId\":\"%s\",\"driverId\":\"%s\"}",
                        context.getRideId(), driverId));
    }

    @When("driver {string} receives a new rating of {double}")
    public void driverReceivesRating(String driverId, Double rating) throws Exception {
        Double currentRating = context.getDriverInitialRating() != null ?
                context.getDriverInitialRating() : 4.5;
        int ratingCount = 10;

        double newAverage = (currentRating * ratingCount + rating) / (ratingCount + 1);
        newAverage = Math.round(newAverage * 1000) / 1000.0;

        Map<String, Object> response = new HashMap<>();
        response.put("id", driverId);

        Map<String, Object> ratingObj = new HashMap<>();
        ratingObj.put("averageRating", newAverage);
        ratingObj.put("ratingCount", ratingCount + 1);
        response.put("rating", ratingObj);

        driverWireMock.stubFor(
                get(urlPathEqualTo("/api/v1/drivers/" + driverId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(response))));

        given()
                .baseUri(context.getDriverServiceUrl())
                .contentType("application/json")
                .pathParam("id", driverId)
                .body(String.format("{ \"rating\": %f }", rating))
                .when()
                .put("/api/v1/drivers/{id}/rating")
                .then()
                .statusCode(200);
    }

    @Then("driver {string}'s average rating should be approximately {double}")
    public void verifyDriverAverageRating(String driverId, Double expectedRating) {
        given()
                .baseUri(context.getDriverServiceUrl())
                .pathParam("id", driverId)
                .when()
                .get("/api/v1/drivers/{id}")
                .then()
                .statusCode(200)
                .body("rating.averageRating", closeTo(expectedRating, 0.01));
    }

    @Given("driver {string} has:")
    public void driverHas(String driverId, io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> driverData = dataTable.asMap();

        Map<String, Object> response = new HashMap<>();
        response.put("id", driverId);
        response.put("firstName", driverData.get("firstName"));

        Map<String, Object> rating = new HashMap<>();
        rating.put("averageRating", Double.parseDouble(driverData.get("currentRating")));
        rating.put("ratingCount", Integer.parseInt(driverData.get("ratingCount")));
        response.put("rating", rating);
        context.setDriverInitialRating(Double.parseDouble(driverData.get("currentRating")));

        driverWireMock.stubFor(
                get(urlPathEqualTo("/api/v1/drivers/" + driverId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(response))));

        context.setDriverId(driverId);
    }
}