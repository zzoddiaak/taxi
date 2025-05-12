package driver_service.driver_service.component;

import driver_service.driver_service.dto.driver.DriverRequestDto;
import driver_service.driver_service.dto.driver.DriverResponseDto;
import driver_service.driver_service.dto.car.CarDto;
import driver_service.driver_service.exception.car.CarAlreadyAssignedException;
import driver_service.driver_service.service.api.DriverService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class DriverStepDefinitions {

    @Autowired
    private TestContext testContext;

    @Autowired
    private DriverService driverService;

    @Given("a car with model {string} and plate number {string}")
    public void a_car_with_model_and_plate_number(String model, String plateNumber) {
        CarDto carDto = CarDto.builder()
                .model(model)
                .plateNumber(plateNumber)
                .build();
        testContext.setCarDto(carDto);
    }

    @When("I create a driver with first name {string}, last name {string} and assign the car")
    public void i_create_a_driver_with_first_name_last_name_and_assign_the_car(String firstName, String lastName) {
        DriverRequestDto driverRequest = DriverRequestDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email("john.doe@example.com")
                .phoneNumber("+123456789")
                .licenseNumber("DL12345")
                .car(testContext.getCarDto())
                .passengerRating(4.5)
                .build();

        testContext.setDriverResponse(driverService.createDriver(driverRequest));
    }

    @Then("the driver should be created with the assigned car")
    public void the_driver_should_be_created_with_the_assigned_car() {
        DriverResponseDto response = testContext.getDriverResponse();
        assertNotNull(response.getId());
        assertEquals(testContext.getCarDto().getPlateNumber(), response.getCar().getPlateNumber());
    }

    @Then("the driver's initial average rating should be {double}")
    public void the_driver_s_initial_average_rating_should_be(Double expectedRating) {
        assertEquals(expectedRating, testContext.getDriverResponse().getRating().getAverageRating());
    }

    @Given("an existing driver with id {long} and current average rating {double} from {int} ratings")
    public void an_existing_driver_with_id_and_current_average_rating_from_ratings(Long id, Double rating, Integer count) {
        testContext.setDriverId(id);
        testContext.setCurrentRating(rating);
        testContext.setRatingCount(count);
    }

    @When("I update the driver's rating with value {double}")
    public void i_update_the_driver_s_rating_with_value(Double rating) {
        driverService.updateDriverRating(testContext.getDriverId(), rating.floatValue());
    }

    @Then("the driver's new average rating should be {double}")
    public void the_driver_s_new_average_rating_should_be(Double expectedRating) {
        DriverResponseDto driver = driverService.getDriverById(testContext.getDriverId());
        assertEquals(expectedRating, driver.getRating().getAverageRating(), 0.001);
    }

    @Then("the rating count should be {int}")
    public void the_rating_count_should_be(Integer expectedCount) {
        DriverResponseDto driver = driverService.getDriverById(testContext.getDriverId());
        assertEquals(expectedCount, driver.getRating().getRatingCount());
    }

    @Given("an existing driver with id {long}")
    public void an_existing_driver_with_id(Long id) {
        testContext.setDriverId(id);
    }

    @When("I get the driver by id {long}")
    public void i_get_the_driver_by_id(Long id) {
        testContext.setDriverResponse(driverService.getDriverById(id));
    }

    @Then("I should receive the driver's details including car information")
    public void i_should_receive_the_driver_s_details_including_car_information() {
        assertNotNull(testContext.getDriverResponse());
        assertNotNull(testContext.getDriverResponse().getCar());
    }

    @Given("a car already assigned to driver with id {long}")
    public void a_car_already_assigned_to_driver_with_id(Long driverId) {
        testContext.setExistingDriverId(driverId);
    }

    @When("I try to assign the same car to driver with id {long}")
    public void i_try_to_assign_the_same_car_to_driver_with_id(Long driverId) {
        try {
            DriverRequestDto request = DriverRequestDto.builder()
                    .firstName("New")
                    .lastName("Driver")
                    .car(testContext.getCarDto())
                    .build();
            driverService.createDriver(request);
        } catch (CarAlreadyAssignedException e) {
            testContext.setException(e);
        }
    }

    @Then("the operation should fail with {string} error")
    public void the_operation_should_fail_with_error(String expectedMessage) {
        assertNotNull(testContext.getException());
        assertEquals(expectedMessage, testContext.getException().getMessage());
    }

    @When("I update the driver's last name to {string} and phone number to {string}")
    public void i_update_the_driver_s_last_name_to_and_phone_number_to(String lastName, String phoneNumber) {
        DriverRequestDto request = DriverRequestDto.builder()
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .build();
        testContext.setDriverResponse(driverService.updateDriver(testContext.getDriverId(), request));
    }

    @Then("the driver's information should be updated accordingly")
    public void the_driver_s_information_should_be_updated_accordingly() {
        DriverResponseDto updatedDriver = driverService.getDriverById(testContext.getDriverId());
        assertEquals("Smith", updatedDriver.getLastName());
        assertEquals("+123456789", updatedDriver.getPhoneNumber());
    }
}