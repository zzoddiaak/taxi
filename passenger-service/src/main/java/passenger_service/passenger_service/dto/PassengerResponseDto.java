package passenger_service.passenger_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import passenger_service.passenger_service.dto.financial.FinancialDataDto;
import passenger_service.passenger_service.dto.rating.RatingDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private RatingDto rating;
    private FinancialDataDto financialData;
    private Double driverRating;
}
