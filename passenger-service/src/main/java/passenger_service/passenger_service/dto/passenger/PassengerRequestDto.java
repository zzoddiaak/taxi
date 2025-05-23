package passenger_service.passenger_service.dto.passenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Double balance;
    private String cardNumber;
    private String cardExpiryDate;
    private String cardCvv;
    private String promo;
    private Double driverRating;
}
