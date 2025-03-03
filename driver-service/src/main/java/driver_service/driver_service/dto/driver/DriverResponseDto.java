package driver_service.driver_service.dto.driver;

import driver_service.driver_service.dto.rating.RatingDto;
import driver_service.driver_service.dto.car.CarDto;
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
    private String phoneNumber;
    private String licenseNumber;
    private CarDto car;
    private RatingDto rating;
}