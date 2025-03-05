package payment_service.payment_service.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    private Long rideId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String promoCode;
}

