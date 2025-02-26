package driver_service.driver_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private String carModel;
    private String carPlateNumber;
    private Double passengerRating;
}