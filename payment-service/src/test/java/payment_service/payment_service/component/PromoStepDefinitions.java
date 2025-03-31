package payment_service.payment_service.component;

import payment_service.payment_service.component.TestContext;
import payment_service.payment_service.dto.promo.PromoCodeRequestDto;
import payment_service.payment_service.dto.promo.PromoCodeResponseDto;
import payment_service.payment_service.dto.promo.DiscountRequestDto;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import payment_service.payment_service.service.impl.PromoCodeService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PromoStepDefinitions {

    @Autowired
    private TestContext testContext;

    @Autowired
    private PromoCodeService promoCodeService;

    @Given("a promo code request with code {string} and {int}% discount")
    public void a_promo_code_request_with_code_and_discount(String code, Integer discount) {
        PromoCodeRequestDto request = PromoCodeRequestDto.builder()
                .code(code)
                .discountPercentage(BigDecimal.valueOf(discount))
                .build();
        testContext.setPromoCodeRequest(request);
    }

    @When("I create the promo code")
    public void i_create_the_promo_code() {
        testContext.setPromoCodeResponse(
                promoCodeService.createPromoCode(testContext.getPromoCodeRequest())
        );
    }

    @When("I apply {string} to amount {double}")
    public void i_apply_to_amount(String promoCode, Double amount) {
        DiscountRequestDto request = new DiscountRequestDto(
                BigDecimal.valueOf(amount), promoCode
        );
        testContext.setDiscountedAmount(
                promoCodeService.applyDiscount(request.getAmount(), request.getPromoCode())
        );
    }

    @When("I get promo code by code {string}")
    public void i_get_promo_code_by_code(String code) {
        // Сохраняем сущность в контекст
        testContext.setPromoCodeEntity(
                promoCodeService.getPromoCodeByCode(code)
        );

        PromoCodeResponseDto response = PromoCodeResponseDto.builder()
                .id(testContext.getPromoCodeEntity().getId())
                .code(testContext.getPromoCodeEntity().getCode())
                .discountPercentage(testContext.getPromoCodeEntity().getDiscountPercentage())
                .expirationDate(testContext.getPromoCodeEntity().getExpirationDate())
                .build();

        testContext.setPromoCodeResponse(response);
    }

    @Then("the promo code should be created with specified discount")
    public void the_promo_code_should_be_created_with_specified_discount() {
        assertNotNull(testContext.getPromoCodeResponse().getId());
        assertEquals(
                testContext.getPromoCodeRequest().getDiscountPercentage(),
                testContext.getPromoCodeResponse().getDiscountPercentage()
        );
    }

    @Then("the discounted amount should be {double}")
    public void the_discounted_amount_should_be(Double expectedAmount) {
        assertEquals(0,
                BigDecimal.valueOf(expectedAmount).compareTo(testContext.getDiscountedAmount()));
    }

    @Then("I should receive the promo code details")
    public void i_should_receive_the_promo_code_details() {
        assertNotNull(testContext.getPromoCodeResponse());
        assertNotNull(testContext.getPromoCodeResponse().getCode());
    }
}