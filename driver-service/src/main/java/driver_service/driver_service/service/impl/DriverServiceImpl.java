package driver_service.driver_service.service.impl;

import driver_service.driver_service.config.mapper.DtoMapper;
import driver_service.driver_service.dto.DriverRequestDto;
import driver_service.driver_service.dto.DriverResponseDto;
import driver_service.driver_service.entity.Driver;
import driver_service.driver_service.repository.DriverRepository;
import driver_service.driver_service.service.api.DriverService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import driver_service.driver_service.exception.driver.DriverNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository repository;
    private final DtoMapper mapper;

    @Override
    public DriverResponseDto createDriver(DriverRequestDto driverRequestDto) {
        Driver driver = mapper.convertToEntity(driverRequestDto, Driver.class);
        Driver savedDriver = repository.save(driver);
        return mapper.convertToDto(savedDriver, DriverResponseDto.class);
    }

    @Override
    public DriverResponseDto getDriverById(Long id) {
        Driver driver = repository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));
        return mapper.convertToDto(driver, DriverResponseDto.class);
    }

    @Override
    public List<DriverResponseDto> getAllDrivers() {
        List<Driver> drivers = repository.findAll();
        return drivers.stream()
                .map(driver -> mapper.convertToDto(driver, DriverResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public DriverResponseDto updateDriver(Long id, DriverRequestDto driverRequestDto) {
        Driver driver = repository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

        driver.setFirstName(driverRequestDto.getFirstName());
        driver.setLastName(driverRequestDto.getLastName());
        driver.setEmail(driverRequestDto.getEmail());
        driver.setPhoneNumber(driverRequestDto.getPhoneNumber());
        driver.setCarModel(driverRequestDto.getCarModel());
        driver.setLicenseNumber(driverRequestDto.getLicenseNumber());
        driver.setCarPlateNumber(driverRequestDto.getCarPlateNumber());

        Driver updatedDriver = repository.save(driver);
        return mapper.convertToDto(updatedDriver, DriverResponseDto.class);
    }

    @Override
    public void deleteDriver(Long id) {
        Driver driver = repository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));
        repository.delete(driver);

    }
}
