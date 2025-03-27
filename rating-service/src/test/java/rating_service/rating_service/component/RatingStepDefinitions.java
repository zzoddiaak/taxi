package rating_service.rating_service.component;

import rating_service.rating_service.dto.RatingRequestDto;
import rating_service.rating_service.exception.rating.RatingNotFoundException;
import rating_service.rating_service.service.api.RatingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;


import static org.junit.jupiter.api.Assertions.*;

public class RatingStepDefinitions {

    @Autowired
    private TestContext testContext;

    @Autowired
    private RatingService ratingService;

    @Given("a driver with id {long} and current rating {double} from {int} ratings")
    public void a_driver_with_id_and_current_rating_from_ratings(Long driverId, Double rating, Integer count) {
        testContext.setDriverId(driverId);
        testContext.setDriverCurrentRating(rating);
        testContext.setDriverRatingCount(count);
    }

    @Given("a passenger with id {long} and current rating {double} from {int} ratings")
    public void a_passenger_with_id_and_current_rating_from_ratings(Long passengerId, Double rating, Integer count) {
        testContext.setPassengerId(passengerId);
        testContext.setPassengerCurrentRating(rating);
        testContext.setPassengerRatingCount(count);
    }

    @When("I create a rating with driverId {long}, passengerId {long}, value {double} and comment {string}")
    public void i_create_a_rating_with_driver_id_passenger_id_value_and_comment(
            Long driverId, Long passengerId, Double rating, String comment) {
        RatingRequestDto request = RatingRequestDto.builder()
                .driverId(driverId)
                .passengerId(passengerId)
                .rating(rating.floatValue())
                .comment(comment)
                .build();

        testContext.setRatingResponse(ratingService.createRating(request));
    }

    @Then("the rating should be created with the provided data")
    public void the_rating_should_be_created_with_the_provided_data() {
        assertNotNull(testContext.getRatingResponse().getId());
        assertEquals(testContext.getDriverId(), testContext.getRatingResponse().getDriverId());
        assertEquals("Excellent service", testContext.getRatingResponse().getComment());
        assertNotNull(testContext.getRatingResponse().getCreatedAt());
    }

    @Then("the driver's rating should be updated to {double}")
    public void the_driver_s_rating_should_be_updated_to(Double expectedRating) {

        assertEquals(expectedRating, testContext.getDriverCurrentRating(), 0.01);
    }

    @Then("the passenger's rating should be updated to {double}")
    public void the_passenger_s_rating_should_be_updated_to(Double expectedRating) {
        assertEquals(expectedRating, testContext.getPassengerCurrentRating(), 0.01);
    }

    @Given("an existing rating with id {long} for driver {long} and passenger {long} with value {double}")
    public void an_existing_rating_with_id_for_driver_and_passenger_with_value(
            Long ratingId, Long driverId, Long passengerId, Double rating) {
        testContext.setRatingId(ratingId);
        testContext.setDriverId(driverId);
        testContext.setPassengerId(passengerId);
        testContext.setCurrentRating(rating.floatValue());
    }

    @When("I get the rating by id {long}")
    public void i_get_the_rating_by_id(Long id) {
        testContext.setRatingResponse(ratingService.getRatingById(id));
    }

    @Then("I should receive the rating details including comment and creation timestamp")
    public void i_should_receive_the_rating_details_including_comment_and_creation_timestamp() {
        assertNotNull(testContext.getRatingResponse());
        assertNotNull(testContext.getRatingResponse().getComment());
        assertNotNull(testContext.getRatingResponse().getCreatedAt());
    }

    @Given("an existing rating with id {long} for driver {long} with value {double}")
    public void an_existing_rating_with_id_for_driver_with_value(
            Long ratingId, Long driverId, Double rating) {
        testContext.setRatingId(ratingId);
        testContext.setDriverId(driverId);
        testContext.setCurrentRating(rating.floatValue());
    }

    @When("I update the rating value to {double}")
    public void i_update_the_rating_value_to(Double newRating) {
        RatingRequestDto request = RatingRequestDto.builder()
                .driverId(testContext.getDriverId())
                .rating(newRating.floatValue())
                .build();

        testContext.setRatingResponse(ratingService.updateRating(testContext.getRatingId(), request));
    }

    @Then("the rating should be updated")
    public void the_rating_should_be_updated() {
        assertEquals(testContext.getCurrentRating(),
                testContext.getRatingResponse().getRating(), 0.01);
    }

    @Given("driver {long} has current rating {double} from {int} ratings")
    public void driver_has_current_rating_from_ratings(Long driverId, Double rating, Integer count) {
        testContext.setDriverCurrentRating(rating);
        testContext.setDriverRatingCount(count);
    }

    @When("I delete the rating with id {long}")
    public void i_delete_the_rating_with_id(Long id) {
        ratingService.deleteRating(id);
        testContext.setRatingDeleted(true);
    }

    @Then("the rating should be deleted")
    public void the_rating_should_be_deleted() {
        assertTrue(testContext.isRatingDeleted());
        assertThrows(RatingNotFoundException.class, () -> {
            ratingService.getRatingById(testContext.getRatingId());
        });
    }

    @Then("the driver's rating should be recalculated to {double}")
    public void the_driver_s_rating_should_be_recalculated_to(Double expectedRating) {
        assertEquals(expectedRating, testContext.getDriverCurrentRating(), 0.01);
    }
}