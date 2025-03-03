package driver_service.driver_service.service.api;

import driver_service.driver_service.dto.driver.DriverListResponseDto;
import driver_service.driver_service.dto.driver.DriverRequestDto;
import driver_service.driver_service.dto.driver.DriverResponseDto;

public interface DriverService {

    DriverResponseDto createDriver(DriverRequestDto driverRequestDto);
    DriverResponseDto getDriverById(Long id);
    DriverListResponseDto getAllDrivers();
    DriverResponseDto updateDriver(Long id, DriverRequestDto driverRequestDto);
    void deleteDriver(Long id);

}
