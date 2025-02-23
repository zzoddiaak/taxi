package passenger_service.passenger_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer phoneNumber;
    private Double averageRating;
    private Integer ratingCount;
    private Double balance;
    private String cardNumber;
    private String cardExpiryDate;
    private String cardCvv;
}