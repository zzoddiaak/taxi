package driver_service.driver_service.component;

import driver_service.driver_service.dto.driver.DriverResponseDto;
import driver_service.driver_service.dto.car.CarDto;
import driver_service.driver_service.exception.car.CarAlreadyAssignedException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TestContext {
    private DriverResponseDto driverResponse;
    private CarDto carDto;
    private Long driverId;
    private Long existingDriverId;
    private Double currentRating;
    private Integer ratingCount;
    private CarAlreadyAssignedException exception;
}
