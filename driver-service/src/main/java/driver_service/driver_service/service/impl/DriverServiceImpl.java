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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import driver_service.driver_service.exception.driver.DriverNotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final DtoMapper mapper;

    @Override
    public DriverResponseDto createDriver(DriverRequestDto driverRequestDto) {
        log.debug("Attempting to create driver from DTO: {}", driverRequestDto);
        Car car = mapper.convertToEntity(driverRequestDto.getCar(), Car.class);

        if (car.getId() != null) {
            log.debug("Checking car existence: carId={}", car.getId());
            Car existingCar = carRepository.findById(car.getId())
                    .orElseThrow(() -> {
                        log.error("Car not found: id={}", car.getId());
                        return new CarNotFoundException("Car not found");
                    });

            if (existingCar.getDriver() != null) {
                log.error("Car already assigned to driver: carId={}, driverId={}", existingCar.getId(), existingCar.getDriver().getId());
                throw new CarAlreadyAssignedException("Car is already assigned to another driver");
            }
        }

        Car savedCar = carRepository.save(car);
        log.debug("Car saved: {}", savedCar);

        Driver driver = mapper.convertToEntity(driverRequestDto, Driver.class);
        driver.setCar(savedCar);
        driver.setAverageRating(0.0);
        driver.setRatingCount(0);

        Driver savedDriver = driverRepository.save(driver);
        log.info("Driver created successfully: driverId={}", savedDriver.getId());
        return mapper.convertToDto(savedDriver, DriverResponseDto.class);
    }

    @Override
    public DriverResponseDto getDriverById(Long id) {
        log.debug("Fetching driver by id: {}", id);
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Driver not found: id={}", id);
                    return new DriverNotFoundException("Driver not found with id: " + id);
                });
        return mapper.convertToDto(driver, DriverResponseDto.class);
    }

    @Override
    public DriverListResponseDto getAllDrivers() {
        log.debug("Fetching all drivers");
        List<Driver> drivers = driverRepository.findAll();
        log.info("Fetched {} drivers", drivers.size());

        List<DriverResponseDto> driverResponseDto = drivers.stream()
                .map(driver -> mapper.convertToDto(driver, DriverResponseDto.class))
                .toList();

        return DriverListResponseDto.builder()
                .drivers(driverResponseDto)
                .build();
    }

    @Override
    public DriverResponseDto updateDriver(Long id, DriverRequestDto driverRequestDto) {
        log.info("Updating driver: id={}", id);
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Driver not found during update: id={}", id);
                    return new DriverNotFoundException("Driver not found with id: " + id);
                });

        Car car = mapper.convertToEntity(driverRequestDto.getCar(), Car.class);
        if (car.getId() != null) {
            log.debug("Checking car for update: carId={}", car.getId());
            Car existingCar = carRepository.findById(car.getId())
                    .orElseThrow(() -> {
                        log.error("Car not found during update: carId={}", car.getId());
                        return new CarNotFoundException("Car not found");
                    });

            if (existingCar.getDriver() != null && !existingCar.getDriver().getId().equals(driver.getId())) {
                log.error("Car conflict during update: existingDriverId={}", existingCar.getDriver().getId());
                throw new CarAlreadyAssignedException("Car is already assigned to another driver");
            }
        }

        Car savedCar = carRepository.save(car);
        log.debug("Car updated: {}", savedCar);

        driver.setFirstName(driverRequestDto.getFirstName());
        driver.setLastName(driverRequestDto.getLastName());
        driver.setEmail(driverRequestDto.getEmail());
        driver.setPhoneNumber(driverRequestDto.getPhoneNumber());
        driver.setLicenseNumber(driverRequestDto.getLicenseNumber());
        driver.setCar(savedCar);
        driver.setPassengerRating(driverRequestDto.getPassengerRating());

        Driver updatedDriver = driverRepository.save(driver);
        log.info("Driver updated successfully: driverId={}", id);
        return mapper.convertToDto(updatedDriver, DriverResponseDto.class);
    }

    @Override
    public void deleteDriver(Long id) {
        log.info("Deleting driver: id={}", id);
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Driver not found during deletion: id={}", id);
                    return new DriverNotFoundException("Driver not found with id: " + id);
                });
        driverRepository.delete(driver);
        log.debug("Driver deleted: id={}", id);
    }

    @Override
    public void updateDriverRating(Long id, Float rating) {
        log.info("Updating rating for driver: id={}, newRating={}", id, rating);
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Driver not found during rating update: id={}", id);
                    return new DriverNotFoundException("Driver not found with id: " + id);
                });

        Double currentRating = driver.getAverageRating();
        Integer ratingCount = driver.getRatingCount();
        log.debug("Current rating: {}, rating count: {}", currentRating, ratingCount);

        Double newRating = ((currentRating * ratingCount) + rating) / (ratingCount + 1);
        driver.setAverageRating(newRating);
        driver.setRatingCount(ratingCount + 1);

        driverRepository.save(driver);
        log.info("New rating calculated: {}", newRating);
    }
}