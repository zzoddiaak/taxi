package driver_service.driver_service.integration.service;

import driver_service.driver_service.dto.car.CarDto;
import driver_service.driver_service.dto.driver.DriverRequestDto;
import driver_service.driver_service.dto.driver.DriverResponseDto;
import driver_service.driver_service.service.impl.DriverServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import driver_service.driver_service.entity.Driver;
import driver_service.driver_service.repository.DriverRepository;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(topics = {"ride-start", "ride-end", "ride-acceptance", "passenger-rating-topic"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DriverServiceImplIntegrationTest {

    @Autowired
    private DriverServiceImpl driverService;

    @Autowired
    private DriverRepository driverRepository;



    @Test
    void createDriver_ShouldCreateDriver() {
        CarDto carDto = CarDto.builder()
                .model("Toyota")
                .plateNumber("ABC123")
                .build();

        DriverRequestDto driverRequestDto = DriverRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .licenseNumber("LIC123")
                .car(carDto)
                .build();

        DriverResponseDto result = driverService.createDriver(driverRequestDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("LIC123", result.getLicenseNumber());

        List<Driver> drivers = driverRepository.findAll();
        assertEquals(1, drivers.size());
        assertEquals("John", drivers.get(0).getFirstName());
    }

    @Test
    void getDriverById_ShouldReturnDriver() {
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setLastName("Doe");
        driver.setEmail("john.doe@example.com");
        driver.setPhoneNumber("1234567890");
        driver.setLicenseNumber("LIC123");
        driverRepository.save(driver);

        DriverResponseDto result = driverService.getDriverById(driver.getId());

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("LIC123", result.getLicenseNumber());
    }

    @Test
    void getAllDrivers_ShouldReturnListOfDrivers() {
        Driver driver1 = new Driver();
        driver1.setFirstName("John");
        driver1.setLastName("Doe");
        driver1.setEmail("john.doe@example.com");
        driver1.setPhoneNumber("1234567890");
        driver1.setLicenseNumber("LIC123");
        driverRepository.save(driver1);

        Driver driver2 = new Driver();
        driver2.setFirstName("Jane");
        driver2.setLastName("Doe");
        driver2.setEmail("jane.doe@example.com");
        driver2.setPhoneNumber("0987654321");
        driver2.setLicenseNumber("LIC456");
        driverRepository.save(driver2);

        List<DriverResponseDto> result = driverService.getAllDrivers().getDrivers();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
    }

    @Test
    void updateDriver_ShouldReturnUpdatedDriver() {
        CarDto carDto = CarDto.builder()
                .model("Toyota")
                .plateNumber("ABC123")
                .build();

        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setLastName("Doe");
        driver.setEmail("john.doe@example.com");
        driver.setPhoneNumber("1234567890");
        driver.setLicenseNumber("LIC123");
        driverRepository.save(driver);

        DriverRequestDto updatedDriver = DriverRequestDto.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("john.updated@example.com")
                .phoneNumber("0987654321")
                .licenseNumber("LIC456")
                .car(carDto)
                .build();

        DriverResponseDto result = driverService.updateDriver(driver.getId(), updatedDriver);

        assertNotNull(result);
        assertEquals("John Updated", result.getFirstName());
        assertEquals("Doe Updated", result.getLastName());
        assertEquals("john.updated@example.com", result.getEmail());
        assertEquals("0987654321", result.getPhoneNumber());
        assertEquals("LIC456", result.getLicenseNumber());
    }

    @Test
    void deleteDriver_ShouldDeleteDriver() {
        Driver driver = new Driver();
        driver.setFirstName("John");
        driver.setLastName("Doe");
        driver.setEmail("john.doe@example.com");
        driver.setPhoneNumber("1234567890");
        driver.setLicenseNumber("LIC123");
        driverRepository.save(driver);

        driverService.deleteDriver(driver.getId());

        assertFalse(driverRepository.existsById(driver.getId()));
    }
}