package rides_service.rides_service.e2e.steps;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import rides_service.rides_service.e2e.context.TestContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PaymentSteps {

    @Autowired
    private TestContext context;

    @Autowired
    @Qualifier("paymentWireMock")
    private WireMockServer paymentWireMock;

    @Autowired
    @Qualifier("rideWireMock")
    private WireMockServer rideWireMock;

    @Then("a payment of {string} should be processed")
    public void verifyPaymentProcessed(String amount) {
        if (context.getRideId() == null) {
            throw new IllegalStateException("Ride ID is not set in test context");
        }

        paymentWireMock.stubFor(
                get(urlPathEqualTo("/api/payments/ride/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{\"rideId\":\"%s\",\"amount\":%s,\"status\":\"COMPLETED\"}",
                                        context.getRideId(), amount))
                                .withStatus(200)));

        given()
                .baseUri(context.getPaymentServiceUrl())
                .pathParam("rideId", context.getRideId())
                .when()
                .get("/api/payments/ride/{rideId}")
                .then()
                .statusCode(200)
                .body("amount", equalTo(Float.parseFloat(amount)));
    }

    @Given("a promo code {string} with {int}% discount exists")
    public void createPromoCode(String code, Integer discount) {
        paymentWireMock.stubFor(
                post(urlPathEqualTo("/api/promocodes"))
                        .willReturn(aResponse()
                                .withStatus(201)
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{\"code\":\"%s\",\"discountPercentage\":%d}",
                                        code, discount))));

        given()
                .baseUri(context.getPaymentServiceUrl())
                .contentType("application/json")
                .body(String.format(
                        "{\"code\":\"%s\",\"discountPercentage\":%d,\"expirationDate\":\"2025-12-31T00:00:00\"}",
                        code, discount))
                .when()
                .post("/api/promocodes")
                .then()
                .statusCode(201);
    }

    @Then("the final payment amount should be {string}")
    public void verifyFinalPaymentAmount(String amount) {
        if (context.getRideId() == null) {
            throw new IllegalStateException("Ride ID is not set in test context");
        }

        paymentWireMock.stubFor(
                get(urlPathEqualTo("/api/payments/ride/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{\"rideId\":\"%s\",\"originalAmount\":100.0,\"discountAmount\":25.0,\"finalAmount\":%s}",
                                        context.getRideId(), amount))));

        given()
                .baseUri(context.getPaymentServiceUrl())
                .pathParam("rideId", context.getRideId())
                .when()
                .get("/api/payments/ride/{rideId}")
                .then()
                .statusCode(200)
                .body("finalAmount", equalTo(Float.parseFloat(amount)));
    }

    @Then("a payment record should be created with status {string}")
    public void verifyPaymentStatus(String status) {
        if (context.getRideId() == null) {
            throw new IllegalStateException("Ride ID is not set in test context");
        }

        paymentWireMock.stubFor(
                get(urlPathEqualTo("/api/payments/ride/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{ \"rideId\": \"%s\", \"status\": \"%s\" }",
                                        context.getRideId(), status))));

        given()
                .baseUri(context.getPaymentServiceUrl())
                .pathParam("rideId", context.getRideId())
                .when()
                .get("/api/payments/ride/{rideId}")
                .then()
                .statusCode(200)
                .body("status", equalTo(status));
    }

    @When("passenger {string} requests a ride with promo code {string} for a {string} ride")
    public void passengerRequestsRideWithPromoCode(String passengerId, String promoCode, String rideCost) {
        context.setPassengerId(passengerId);

        rideWireMock.stubFor(
                post(urlPathEqualTo("/api/rides"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{ \"id\": \"12345\", \"passengerId\": \"%s\", \"promoCode\": \"%s\", \"cost\": %s }",
                                        passengerId, promoCode, rideCost))));

        Response response = given()
                .baseUri(context.getRideServiceUrl())
                .contentType("application/json")
                .body(String.format(
                        "{ \"passengerId\": \"%s\", \"promoCode\": \"%s\", \"estimatedCost\": %s }",
                        passengerId, promoCode, rideCost))
                .when()
                .post("/api/rides")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String rideId = response.path("id");
        if (rideId == null) {
            throw new RuntimeException("Ride ID not found in response");
        }
        context.setRideId(rideId);
    }

    @When("passenger {string} requests a ride costing {string}")
    public void passengerRequestsRideCosting(String passengerId, String cost) {
        context.setPassengerId(passengerId);

        rideWireMock.stubFor(
                post(urlPathEqualTo("/api/rides"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{ \"id\": \"12345\", \"passengerId\": \"%s\", \"cost\": %s }",
                                        passengerId, cost))));

        Response response = given()
                .baseUri(context.getRideServiceUrl())
                .contentType("application/json")
                .body(String.format(
                        "{ \"passengerId\": \"%s\", \"estimatedCost\": %s }",
                        passengerId, cost))
                .when()
                .post("/api/rides")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String rideId = response.path("id");
        if (rideId == null) {
            throw new RuntimeException("Ride ID not found in response");
        }
        context.setRideId(rideId);
    }

    @Then("the payment should be rejected with {string} status")
    public void verifyPaymentRejection(String status) {
        if (context.getRideId() == null) {
            throw new IllegalStateException("Ride ID is not set in test context");
        }

        paymentWireMock.stubFor(
                get(urlPathEqualTo("/api/payments/ride/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{ \"rideId\": \"%s\", \"status\": \"%s\" }",
                                        context.getRideId(), status))));

        given()
                .baseUri(context.getPaymentServiceUrl())
                .pathParam("rideId", context.getRideId())
                .when()
                .get("/api/payments/ride/{rideId}")
                .then()
                .statusCode(200)
                .body("status", equalTo(status));
    }

    @Then("any pending payment should be voided")
    public void verifyPaymentVoided() {
        if (context.getRideId() == null) {
            throw new IllegalStateException("Ride ID is not set in test context");
        }

        paymentWireMock.stubFor(
                get(urlPathEqualTo("/api/payments/ride/" + context.getRideId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(
                                        "{ \"rideId\": \"%s\", \"status\": \"VOIDED\" }",
                                        context.getRideId()))));

        given()
                .baseUri(context.getPaymentServiceUrl())
                .pathParam("rideId", context.getRideId())
                .when()
                .get("/api/payments/ride/{rideId}")
                .then()
                .statusCode(200)
                .body("status", equalTo("VOIDED"));
    }
}