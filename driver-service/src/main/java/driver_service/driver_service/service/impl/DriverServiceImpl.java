package driver_service.driver_service.service.impl;

import driver_service.driver_service.config.mapper.DtoMapper;
import driver_service.driver_service.dto.DriverListResponseDto;
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
        driver.setAverageRating(0.0);
        driver.setRatingCount(0);
        Driver savedDriver = repository.save(driver);
        return mapper.convertToDto(savedDriver, DriverResponseDto.class);
    }

    @Override
    public DriverResponseDto getDriverById(Long id) {
        Driver driver = repository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format("Driver not found with id: " + id)));
        return mapper.convertToDto(driver, DriverResponseDto.class);
    }

    @Override
    public DriverListResponseDto getAllDrivers() {
        List<Driver> drivers = repository.findAll();

        List<DriverResponseDto> driverResponseDto = drivers.stream()
                .map(driver -> mapper.convertToDto(driver, DriverResponseDto.class))
                .toList();

        return DriverListResponseDto.builder()
                .drivers(driverResponseDto)
                .build();

    }

    @Override
    public DriverResponseDto updateDriver(Long id, DriverRequestDto driverRequestDto) {
        Driver driver = repository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format("Driver not found with id: " + id)));

        driver.setFirstName(driverRequestDto.getFirstName());
        driver.setLastName(driverRequestDto.getLastName());
        driver.setEmail(driverRequestDto.getEmail());
        driver.setPhoneNumber(driverRequestDto.getPhoneNumber());
        driver.setCarModel(driverRequestDto.getCarModel());
        driver.setLicenseNumber(driverRequestDto.getLicenseNumber());
        driver.setCarPlateNumber(driverRequestDto.getCarPlateNumber());
        driver.setPassengerRating(driverRequestDto.getPassengerRating());

        Driver updatedDriver = repository.save(driver);
        return mapper.convertToDto(updatedDriver, DriverResponseDto.class);
    }

    @Override
    public void deleteDriver(Long id) {
        Driver driver = repository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format("Driver not found with id: " + id)));
        repository.delete(driver);
    }
}