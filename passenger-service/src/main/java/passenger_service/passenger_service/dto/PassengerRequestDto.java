package passenger_service.passenger_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}