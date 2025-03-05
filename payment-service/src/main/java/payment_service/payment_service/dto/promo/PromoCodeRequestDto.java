package payment_service.payment_service.dto.promo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeRequestDto {

    private String code;
    private BigDecimal discountPercentage;
    private LocalDateTime expirationDate;
}

