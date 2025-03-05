package passenger_service.passenger_service.dto.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialDataDto {
    private Double balance;
    private String cardNumber;
    private String cardExpiryDate;
    private String cardCvv;
    private String promo;
}
