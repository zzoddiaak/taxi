package driver_service.driver_service.service.impl;

import driver_service.driver_service.config.mapper.DtoMapper;
import driver_service.driver_service.dto.driver.DriverListResponseDto;
import driver_service.driver_service.dto.driver.DriverRequestDto;
import driver_service.driver_service.dto.driver.DriverResponseDto;
import driver_service.driver_service.entity.Car;
import driver_service.driver_service.entity.Driver;
import driver_service.driver_service.exception.car.CarAlreadyAssignedException;
import driver_service.driver_service.exception.car.CarNotFoundException;
import driver_service.driver_service.repository.CarRepository;
import driver_service.driver_service.repository.DriverRepository;
import driver_service.driver_service.service.api.DriverService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import driver_service.driver_service.exception.driver.DriverNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final DtoMapper mapper;

    @Override
    public DriverResponseDto createDriver(DriverRequestDto driverRequestDto) {
        Car car = mapper.convertToEntity(driverRequestDto.getCar(), Car.class);
        if (car.getId() != null) {
            Car existingCar = carRepository.findById(car.getId())
                    .orElseThrow(() -> new CarNotFoundException(String.format("Car not found")));
            if (existingCar.getDriver() != null) {
                throw new CarAlreadyAssignedException(String.format("Car is already assigned to another driver"));
            }
        }

        Car savedCar = carRepository.save(car);

        Driver driver = mapper.convertToEntity(driverRequestDto, Driver.class);
        driver.setCar(savedCar);
        driver.setAverageRating(0.0);
        driver.setRatingCount(0);

        Driver savedDriver = driverRepository.save(driver);
        return mapper.convertToDto(savedDriver, DriverResponseDto.class);
    }

    @Override
    public DriverResponseDto getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format("Driver not found with id: " + id)));
        return mapper.convertToDto(driver, DriverResponseDto.class);
    }

    @Override
    public DriverListResponseDto getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();

        List<DriverResponseDto> driverResponseDto = drivers.stream()
                .map(driver -> mapper.convertToDto(driver, DriverResponseDto.class))
                .toList();

        return DriverListResponseDto.builder()
                .drivers(driverResponseDto)
                .build();
    }

    @Override
    public DriverResponseDto updateDriver(Long id, DriverRequestDto driverRequestDto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format("Driver not found with id: " + id)));

        Car car = mapper.convertToEntity(driverRequestDto.getCar(), Car.class);
        if (car.getId() != null) {
            Car existingCar = carRepository.findById(car.getId())
                    .orElseThrow(() -> new CarNotFoundException(String.format("Car not found")));
            if (existingCar.getDriver() != null && !existingCar.getDriver().getId().equals(driver.getId())) {
                throw new CarAlreadyAssignedException(String.format("Car is already assigned to another driver"));
            }
        }

        Car savedCar = carRepository.save(car);

        driver.setFirstName(driverRequestDto.getFirstName());
        driver.setLastName(driverRequestDto.getLastName());
        driver.setEmail(driverRequestDto.getEmail());
        driver.setPhoneNumber(driverRequestDto.getPhoneNumber());
        driver.setLicenseNumber(driverRequestDto.getLicenseNumber());
        driver.setCar(savedCar);
        driver.setPassengerRating(driverRequestDto.getPassengerRating());

        Driver updatedDriver = driverRepository.save(driver);
        return mapper.convertToDto(updatedDriver, DriverResponseDto.class);
    }

    @Override
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format("Driver not found with id: " + id)));
        driverRepository.delete(driver);
    }

    @Override
    public void updateDriverRating(Long id, Float rating) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

        Double currentRating = driver.getAverageRating();
        Integer ratingCount = driver.getRatingCount();

        Double newRating = ((currentRating * ratingCount) + rating) / (ratingCount + 1);

        driver.setAverageRating(newRating);
        driver.setRatingCount(ratingCount + 1);

        driverRepository.save(driver);
    }
}