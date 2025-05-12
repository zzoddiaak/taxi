package rides_service.rides_service.e2e.steps;

import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import rides_service.rides_service.e2e.context.TestContext;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

public class RatingVerificationSteps {

    @Autowired
    private TestContext context;


    @Then("the new average rating should be approximately {double}")
    public void verifyApproximateAverageRating(Double expectedRating) {
        String id = context.getPassengerId() != null ? context.getPassengerId() : context.getDriverId();
        String serviceUrl = context.getPassengerId() != null ?
                context.getRatingServiceUrl() : context.getDriverServiceUrl();
        String path = context.getPassengerId() != null ?
                "/api/ratings/average/{id}" : "/api/v1/drivers/{id}";

        given()
                .baseUri(serviceUrl)
                .pathParam("id", id)
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .body(context.getPassengerId() != null ?
                                "averageRating.toString()" :
                                "rating.averageRating.toString()",
                        equalTo(String.valueOf(expectedRating)));
    }

    @Then("driver {string}'s average rating should be updated")
    public void verifyDriverRatingUpdated(String driverId) {
        given()
                .baseUri(context.getDriverServiceUrl())
                .pathParam("id", driverId)
                .when()
                .get("/api/v1/drivers/{id}")
                .then()
                .statusCode(200)
                .body("rating.averageRating.toString()",
                        equalTo("4.5"));
    }

    @Then("passenger {string}'s average rating should be updated")
    public void verifyPassengerRatingUpdated(String passengerId) {
        given()
                .baseUri(context.getPassengerServiceUrl())
                .pathParam("id", passengerId)
                .when()
                .get("/api/passengers/{id}")
                .then()
                .statusCode(200)
                .body("rating.averageRating.toString()",
                        equalTo("4.0"));
    }
}