package driver_service.driver_service.service.api;

import driver_service.driver_service.dto.DriverRequestDto;
import driver_service.driver_service.dto.DriverResponseDto;

import java.util.List;

public interface DriverService {

    DriverResponseDto createDriver(DriverRequestDto driverRequestDto);
    DriverResponseDto getDriverById(Long id);
    List<DriverResponseDto> getAllDrivers();
    DriverResponseDto updateDriver(Long id, DriverRequestDto driverRequestDto);
    void deleteDriver(Long id);

}
