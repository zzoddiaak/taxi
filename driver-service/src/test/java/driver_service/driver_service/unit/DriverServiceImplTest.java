package driver_service.driver_service.unit;

import driver_service.driver_service.config.mapper.DtoMapper;
import driver_service.driver_service.dto.driver.DriverListResponseDto;
import driver_service.driver_service.dto.driver.DriverRequestDto;
import driver_service.driver_service.dto.driver.DriverResponseDto;
import driver_service.driver_service.entity.Car;
import driver_service.driver_service.entity.Driver;
import driver_service.driver_service.exception.car.CarAlreadyAssignedException;
import driver_service.driver_service.exception.driver.DriverNotFoundException;
import driver_service.driver_service.repository.CarRepository;
import driver_service.driver_service.repository.DriverRepository;
import driver_service.driver_service.service.impl.DriverServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private DtoMapper mapper;

    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    void createDriver_ShouldCreateDriver_WhenValidRequest() {
        DriverRequestDto requestDto = new DriverRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        Car car = new Car();
        car.setId(1L);

        Driver driver = new Driver();
        driver.setId(1L);
        driver.setFirstName("John");
        driver.setLastName("Doe");
        driver.setCar(car);

        when(mapper.convertToEntity(requestDto.getCar(), Car.class)).thenReturn(car);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(mapper.convertToEntity(requestDto, Driver.class)).thenReturn(driver);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(mapper.convertToDto(driver, DriverResponseDto.class)).thenReturn(new DriverResponseDto());

        DriverResponseDto result = driverService.createDriver(requestDto);


        assertNotNull(result);
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void createDriver_ShouldThrowException_WhenCarAlreadyAssigned() {
        DriverRequestDto requestDto = new DriverRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        Car car = new Car();
        car.setId(1L);
        car.setDriver(new Driver());

        when(mapper.convertToEntity(requestDto.getCar(), Car.class)).thenReturn(car);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        assertThrows(CarAlreadyAssignedException.class, () -> driverService.createDriver(requestDto));
    }

    @Test
    void getDriverById_ShouldReturnDriver_WhenDriverExists() {
        Long driverId = 1L;
        Driver driver = new Driver();
        driver.setId(driverId);

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(mapper.convertToDto(driver, DriverResponseDto.class)).thenReturn(new DriverResponseDto());

        DriverResponseDto result = driverService.getDriverById(driverId);

        assertNotNull(result);
        verify(driverRepository, times(1)).findById(driverId);
    }

    @Test
    void getDriverById_ShouldThrowException_WhenDriverNotFound() {
        Long driverId = 1L;
        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.getDriverById(driverId));
    }

    @Test
    void getAllDrivers_ShouldReturnListOfDrivers() {
        Driver driver = new Driver();
        driver.setId(1L);

        when(driverRepository.findAll()).thenReturn(List.of(driver));
        when(mapper.convertToDto(driver, DriverResponseDto.class)).thenReturn(new DriverResponseDto());

        DriverListResponseDto result = driverService.getAllDrivers();

        assertNotNull(result);
        assertEquals(1, result.getDrivers().size());
        verify(driverRepository, times(1)).findAll();
    }

    @Test
    void updateDriver_ShouldUpdateDriver_WhenValidRequest() {
        Long driverId = 1L;
        DriverRequestDto requestDto = new DriverRequestDto();
        requestDto.setFirstName("Updated John");
        requestDto.setLastName("Updated Doe");

        Driver existingDriver = new Driver();
        existingDriver.setId(driverId);
        existingDriver.setFirstName("John");
        existingDriver.setLastName("Doe");

        Car car = new Car();
        car.setId(1L);

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(existingDriver));
        when(mapper.convertToEntity(requestDto.getCar(), Car.class)).thenReturn(car);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(driverRepository.save(existingDriver)).thenReturn(existingDriver);
        when(mapper.convertToDto(existingDriver, DriverResponseDto.class)).thenReturn(new DriverResponseDto());

        DriverResponseDto result = driverService.updateDriver(driverId, requestDto);

        assertNotNull(result);
        assertEquals("Updated John", existingDriver.getFirstName());
        assertEquals("Updated Doe", existingDriver.getLastName());
        verify(driverRepository, times(1)).save(existingDriver);
    }

    @Test
    void deleteDriver_ShouldDeleteDriver_WhenDriverExists() {
        Long driverId = 1L;
        Driver driver = new Driver();
        driver.setId(driverId);

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        doNothing().when(driverRepository).delete(driver);

        driverService.deleteDriver(driverId);

        verify(driverRepository, times(1)).delete(driver);
    }

    @Test
    void updateDriverRating_ShouldUpdateRating_WhenDriverExists() {
        Long driverId = 1L;
        Driver driver = new Driver();
        driver.setId(driverId);
        driver.setAverageRating(4.0);
        driver.setRatingCount(1);

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));

        driverService.updateDriverRating(driverId, 5.0f);

        assertEquals(4.5, driver.getAverageRating());
        assertEquals(2, driver.getRatingCount());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void updateDriverRating_ShouldThrowException_WhenDriverNotFound() {
        Long driverId = 1L;
        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.updateDriverRating(driverId, 5.0f));
    }
}