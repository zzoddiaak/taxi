package passenger_service.passenger_service.component;

import passenger_service.passenger_service.component.TestContext;
import passenger_service.passenger_service.dto.passenger.PassengerRequestDto;
import passenger_service.passenger_service.dto.passenger.PassengerResponseDto;
import passenger_service.passenger_service.dto.financial.FinancialDataDto;
import passenger_service.passenger_service.exception.passenger.InsufficientBalanceException;
import passenger_service.passenger_service.service.api.PassengerService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class PassengerStepDefinitions {

    @Autowired
    private TestContext testContext;

    @Autowired
    private PassengerService passengerService;

    @Given("a passenger with first name {string}, last name {string} and email {string}")
    public void a_passenger_with_first_name_last_name_and_email(String firstName, String lastName, String email) {
        PassengerRequestDto request = PassengerRequestDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber("+123456789")
                .driverRating(4.5)
                .build();
        testContext.setPassengerRequest(request);
    }

    @Given("financial data with balance {double} and card number {string}")
    public void financial_data_with_balance_and_card_number(Double balance, String cardNumber) {
        testContext.getPassengerRequest().setBalance(balance);
        testContext.getPassengerRequest().setCardNumber(cardNumber);
        testContext.getPassengerRequest().setCardExpiryDate("12/25");
        testContext.getPassengerRequest().setCardCvv("123");
    }

    @When("I create the passenger")
    public void i_create_the_passenger() {
        testContext.setPassengerResponse(
                passengerService.createPassenger(testContext.getPassengerRequest())
        );
    }

    @Then("the passenger should be created with the provided financial data")
    public void the_passenger_should_be_created_with_the_provided_financial_data() {
        PassengerResponseDto response = testContext.getPassengerResponse();
        assertNotNull(response.getId());
        assertEquals(testContext.getPassengerRequest().getCardNumber(),
                response.getFinancialData().getCardNumber());
    }

    @Then("the passenger's initial average rating should be {double}")
    public void the_passenger_s_initial_average_rating_should_be(Double expectedRating) {
        assertEquals(expectedRating, testContext.getPassengerResponse().getRating().getAverageRating());
    }

    @Given("an existing passenger with id {long} and current average rating {double} from {int} ratings")
    public void an_existing_passenger_with_id_and_current_average_rating_from_ratings(
            Long id, Double rating, Integer count) {
        testContext.setPassengerId(id);
        testContext.setCurrentRating(rating);
        testContext.setRatingCount(count);
    }

    @When("I update the passenger's rating with value {double}")
    public void i_update_the_passenger_s_rating_with_value(Double rating) {
        passengerService.updatePassengerRating(testContext.getPassengerId(), rating.floatValue());
    }

    @Then("the passenger's new average rating should be {double}")
    public void the_passenger_s_new_average_rating_should_be(Double expectedRating) {
        PassengerResponseDto passenger = passengerService.getPassengerById(testContext.getPassengerId());
        assertEquals(expectedRating, passenger.getRating().getAverageRating(), 0.001);
    }

    @Then("the rating count should be {int}")
    public void the_rating_count_should_be(Integer expectedCount) {
        PassengerResponseDto passenger = passengerService.getPassengerById(testContext.getPassengerId());
        assertEquals(expectedCount, passenger.getRating().getRatingCount());
    }

    @Given("an existing passenger with id {long} and balance {double}")
    public void an_existing_passenger_with_id_and_balance(Long id, Double balance) {
        testContext.setPassengerId(id);
        testContext.setCurrentBalance(balance);
    }

    @When("I get the passenger by id {long}")
    public void i_get_the_passenger_by_id(Long id) {
        testContext.setPassengerResponse(passengerService.getPassengerById(id));
    }

    @Then("I should receive the passenger's details including financial information")
    public void i_should_receive_the_passenger_s_details_including_financial_information() {
        assertNotNull(testContext.getPassengerResponse());
        assertNotNull(testContext.getPassengerResponse().getFinancialData());
    }

    @Then("the balance should be {double}")
    public void the_balance_should_be(Double expectedBalance) {
        assertEquals(expectedBalance,
                testContext.getPassengerResponse().getFinancialData().getBalance());
    }

    @When("I update the passenger's balance by {double}")
    public void i_update_the_passenger_s_balance_by(Double amount) {
        passengerService.updatePassengerBalance(testContext.getPassengerId(), amount);
    }

    @Then("the passenger's new balance should be {double}")
    public void the_passenger_s_new_balance_should_be(Double expectedBalance) {
        PassengerResponseDto passenger = passengerService.getPassengerById(testContext.getPassengerId());
        assertEquals(expectedBalance, passenger.getFinancialData().getBalance());
    }

    @When("I try to update the passenger's balance by {double}")
    public void i_try_to_update_the_passenger_s_balance_by(Double amount) {
        try {
            passengerService.updatePassengerBalance(testContext.getPassengerId(), amount);
        } catch (InsufficientBalanceException e) {
            testContext.setException(e);
        }
    }

    @Then("the operation should fail with {string} error")
    public void the_operation_should_fail_with_error(String expectedMessage) {
        assertNotNull(testContext.getException());
        assertEquals(expectedMessage, testContext.getException().getMessage());
    }

    @When("I update the passenger's email to {string} and phone number to {string}")
    public void i_update_the_passenger_s_email_to_and_phone_number_to(String email, String phoneNumber) {
        PassengerRequestDto request = testContext.getPassengerRequest();
        if (request == null) {
            request = new PassengerRequestDto();
        }
        request.setEmail(email);
        request.setPhoneNumber(phoneNumber);

        testContext.setPassengerResponse(
                passengerService.updatePassenger(testContext.getPassengerId(), request)
        );
    }

    @Then("the passenger's information should be updated accordingly")
    public void the_passenger_s_information_should_be_updated_accordingly() {
        PassengerResponseDto updatedPassenger = passengerService.getPassengerById(testContext.getPassengerId());
        assertEquals("new.email@example.com", updatedPassenger.getEmail());
        assertEquals("+987654321", updatedPassenger.getPhoneNumber());
    }
}