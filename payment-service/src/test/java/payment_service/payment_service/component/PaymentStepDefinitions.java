package payment_service.payment_service.component;

import payment_service.payment_service.component.TestContext;
import payment_service.payment_service.dto.payment.PaymentRequestDto;
import payment_service.payment_service.dto.payment.PaymentResponseDto;
import payment_service.payment_service.dto.promo.PromoCodeRequestDto;
import payment_service.payment_service.dto.promo.PromoCodeResponseDto;
import payment_service.payment_service.exception.payment.PaymentNotFoundException;
import payment_service.payment_service.service.api.PaymentService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import payment_service.payment_service.service.impl.PromoCodeService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentStepDefinitions {

    @Autowired
    private PromoCodeService promoCodeService;

    @Autowired
    private TestContext testContext;

    @Autowired
    private PaymentService paymentService;

    @Given("a payment request for ride {long} with amount {double} and payment method {string}")
    public void a_payment_request_for_ride_with_amount_and_payment_method(
            Long rideId, Double amount, String paymentMethod) {
        PaymentRequestDto request = PaymentRequestDto.builder()
                .rideId(rideId)
                .passengerId(1L)
                .amount(BigDecimal.valueOf(amount))
                .paymentMethod(paymentMethod)
                .status("pending")
                .build();
        testContext.setPaymentRequest(request);
    }

    @Given("an active promo code {string} with {int}% discount")
    public void an_active_promo_code_with_discount(String code, Integer discount) {
        PromoCodeRequestDto promoRequest = PromoCodeRequestDto.builder()
                .code(code)
                .discountPercentage(BigDecimal.valueOf(discount))
                .build();
        PromoCodeResponseDto createdPromo = promoCodeService.createPromoCode(promoRequest);
        testContext.setPromoCodeResponse(createdPromo);
    }

    @Given("a payment request for ride {long} with amount {double} and promo code {string}")
    public void a_payment_request_for_ride_with_amount_and_promo_code(
            Long rideId, Double amount, String promoCode) {
        PaymentRequestDto request = PaymentRequestDto.builder()
                .rideId(rideId)
                .passengerId(1L)
                .amount(BigDecimal.valueOf(amount))
                .paymentMethod("credit_card")
                .promoCode(promoCode)
                .build();
        testContext.setPaymentRequest(request);
    }

    @Given("an existing cash payment with id {long} and status {string}")
    public void an_existing_cash_payment_with_id_and_status(Long id, String status) {
        PaymentRequestDto request = PaymentRequestDto.builder()
                .rideId(100L)
                .paymentMethod("cash")
                .status(status)
                .amount(BigDecimal.valueOf(50.0))
                .build();
        PaymentResponseDto response = paymentService.createPayment(request);
        testContext.setPaymentId(response.getId());
    }

    @Given("an existing credit card payment with id {long} and status {string}")
    public void an_existing_credit_card_payment_with_id_and_status(Long id, String status) {
        PaymentRequestDto request = PaymentRequestDto.builder()
                .rideId(101L)
                .paymentMethod("credit_card")
                .status(status)
                .amount(BigDecimal.valueOf(75.0))
                .build();
        PaymentResponseDto response = paymentService.createPayment(request);
        testContext.setPaymentId(response.getId());
    }

    @Given("an existing payment for ride {long}")
    public void an_existing_payment_for_ride(Long rideId) {
        PaymentRequestDto request = PaymentRequestDto.builder()
                .rideId(rideId)
                .paymentMethod("credit_card")
                .amount(BigDecimal.valueOf(60.0))
                .build();
        PaymentResponseDto response = paymentService.createPayment(request);
        testContext.setPaymentId(response.getId());
    }

    @When("I create the payment")
    public void i_create_the_payment() {
        testContext.setPaymentResponse(
                paymentService.createPayment(testContext.getPaymentRequest())
        );
    }

    @When("I update the payment status to {string}")
    public void i_update_the_payment_status_to(String status) {
        paymentService.updatePaymentStatus(testContext.getPaymentId(), status);
    }

    @When("I try to update the payment status to {string}")
    public void i_try_to_update_the_payment_status_to(String status) {
        try {
            paymentService.updatePaymentStatus(testContext.getPaymentId(), status);
        } catch (IllegalArgumentException e) {
            testContext.setException(e);
        }
    }

    @When("I get payment by ride ID {long}")
    public void i_get_payment_by_ride_ID(Long rideId) {
        testContext.setPaymentResponse(
                paymentService.getPaymentByRideId(rideId)
        );
    }

    @Then("the payment should be created with status {string}")
    public void the_payment_should_be_created_with_status(String expectedStatus) {
        assertEquals(expectedStatus, testContext.getPaymentResponse().getStatus());
    }

    @Then("the payment amount should be {double}")
    public void the_payment_amount_should_be(Double expectedAmount) {
        assertEquals(0,
                BigDecimal.valueOf(expectedAmount).compareTo(testContext.getPaymentResponse().getAmount()));
    }

    @Then("the promo code should be applied")
    public void the_promo_code_should_be_applied() {
        assertNotNull(testContext.getPaymentResponse().getPromoCode());
    }

    @Then("the payment status should be {string}")
    public void the_payment_status_should_be(String expectedStatus) {
        PaymentResponseDto payment = paymentService.getPaymentById(testContext.getPaymentId());
        assertEquals(expectedStatus, payment.getStatus());
    }

    @Then("the operation should fail with {string} error")
    public void the_operation_should_fail_with_error(String expectedMessage) {
        assertNotNull(testContext.getException());
        assertEquals(expectedMessage, testContext.getException().getMessage());
    }

    @Then("I should receive the payment details")
    public void i_should_receive_the_payment_details() {
        assertNotNull(testContext.getPaymentResponse());
        assertNotNull(testContext.getPaymentResponse().getId());
    }
}