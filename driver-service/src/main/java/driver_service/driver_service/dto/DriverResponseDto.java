package driver_service.driver_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer phoneNumber;
    private Integer licenseNumber;
    private String carModel;
    private String carPlateNumber;

}
