package payment_service.payment_service.component;

import payment_service.payment_service.dto.payment.PaymentRequestDto;
import payment_service.payment_service.dto.payment.PaymentResponseDto;
import payment_service.payment_service.dto.promo.PromoCodeRequestDto;
import payment_service.payment_service.dto.promo.PromoCodeResponseDto;
import payment_service.payment_service.entity.PromoCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Getter
@Setter
@Component
public class TestContext {
    private PaymentRequestDto paymentRequest;
    private PaymentResponseDto paymentResponse;
    private PromoCodeRequestDto promoCodeRequest;
    private PromoCodeResponseDto promoCodeResponse;
    private PromoCode promoCodeEntity;
    private Long paymentId;
    private BigDecimal discountedAmount;
    private IllegalArgumentException exception;
}