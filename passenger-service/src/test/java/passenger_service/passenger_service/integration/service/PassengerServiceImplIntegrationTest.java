package passenger_service.passenger_service.integration.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import passenger_service.passenger_service.dto.passenger.PassengerRequestDto;
import passenger_service.passenger_service.dto.passenger.PassengerResponseDto;
import passenger_service.passenger_service.entity.FinancialData;
import passenger_service.passenger_service.entity.Passenger;

import passenger_service.passenger_service.exception.passenger.FinancialDataNotFoundException;
import passenger_service.passenger_service.exception.passenger.InsufficientBalanceException;
import passenger_service.passenger_service.repository.FinancialDataRepository;
import passenger_service.passenger_service.repository.PassengerRepository;
import passenger_service.passenger_service.service.impl.PassengerServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(topics = {"passenger-rating-topic"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PassengerServiceImplIntegrationTest {

    @Autowired
    private PassengerServiceImpl passengerService;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FinancialDataRepository financialDataRepository;

    @AfterEach
    void tearDown() {
        passengerRepository.deleteAll();
        financialDataRepository.deleteAll();
    }

    @Test
    void createPassenger_ShouldCreatePassenger() {
        PassengerRequestDto passengerRequestDto = new PassengerRequestDto();
        passengerRequestDto.setFirstName("John");
        passengerRequestDto.setLastName("Doe");
        passengerRequestDto.setEmail("john.doe@example.com");
        passengerRequestDto.setPhoneNumber("1234567890");
        passengerRequestDto.setDriverRating(4.5);

        PassengerResponseDto result = passengerService.createPassenger(passengerRequestDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals(4.5, result.getDriverRating());

        List<Passenger> passengers = passengerRepository.findAll();
        assertEquals(1, passengers.size());
        assertEquals("John", passengers.get(0).getFirstName());
    }

    @Test
    void updatePassenger_ShouldUpdatePassenger() {
        Passenger passenger = new Passenger();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPhoneNumber("1234567890");
        passenger.setDriverRating(4.5);
        passengerRepository.save(passenger);

        PassengerRequestDto updatedPassenger = new PassengerRequestDto();
        updatedPassenger.setFirstName("John Updated");
        updatedPassenger.setLastName("Doe Updated");
        updatedPassenger.setEmail("john.updated@example.com");
        updatedPassenger.setPhoneNumber("0987654321");
        updatedPassenger.setDriverRating(5.0);

        PassengerResponseDto result = passengerService.updatePassenger(passenger.getId(), updatedPassenger);

        assertNotNull(result);
        assertEquals("John Updated", result.getFirstName());
        assertEquals("Doe Updated", result.getLastName());
        assertEquals("john.updated@example.com", result.getEmail());
        assertEquals("0987654321", result.getPhoneNumber());
        assertEquals(5.0, result.getDriverRating());
    }

    @Test
    void getPassengerById_ShouldReturnPassenger() {
        Passenger passenger = new Passenger();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPhoneNumber("1234567890");
        passenger.setDriverRating(4.5);
        passengerRepository.save(passenger);

        PassengerResponseDto result = passengerService.getPassengerById(passenger.getId());

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals(4.5, result.getDriverRating());
    }

    @Test
    void getAllPassengers_ShouldReturnListOfPassengers() {
        Passenger passenger1 = new Passenger();
        passenger1.setFirstName("John");
        passenger1.setLastName("Doe");
        passenger1.setEmail("john.doe@example.com");
        passenger1.setPhoneNumber("1234567890");
        passenger1.setDriverRating(4.5);
        passengerRepository.save(passenger1);

        Passenger passenger2 = new Passenger();
        passenger2.setFirstName("Jane");
        passenger2.setLastName("Doe");
        passenger2.setEmail("jane.doe@example.com");
        passenger2.setPhoneNumber("0987654321");
        passenger2.setDriverRating(5.0);
        passengerRepository.save(passenger2);

        List<PassengerResponseDto> result = passengerService.getAllPassengers().getPassengers();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
    }

    @Test
    void deletePassenger_ShouldDeletePassenger() {
        Passenger passenger = new Passenger();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPhoneNumber("1234567890");
        passenger.setDriverRating(4.5);
        passengerRepository.save(passenger);

        passengerService.deletePassenger(passenger.getId());

        assertFalse(passengerRepository.existsById(passenger.getId()));
    }

    @Test
    void updatePassengerRating_ShouldUpdateRating() {
        Passenger passenger = new Passenger();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPhoneNumber("1234567890");
        passenger.setAverageRating(4.0);
        passenger.setRatingCount(1);
        passengerRepository.save(passenger);

        passengerService.updatePassengerRating(passenger.getId(), 5.0f);

        Passenger updatedPassenger = passengerRepository.findById(passenger.getId()).orElseThrow();
        assertEquals(4.5, updatedPassenger.getAverageRating());
        assertEquals(2, updatedPassenger.getRatingCount());
    }

    @Test
    void updatePassengerBalance_ShouldUpdateBalance() {
        Passenger passenger = new Passenger();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPhoneNumber("1234567890");
        passengerRepository.save(passenger);

        FinancialData financialData = new FinancialData();
        financialData.setPassenger(passenger);
        financialData.setBalance(100.0);
        financialDataRepository.save(financialData);

        passengerService.updatePassengerBalance(passenger.getId(), 50.0);

        FinancialData updatedFinancialData = financialDataRepository.findById(financialData.getId()).orElseThrow();
        assertEquals(50.0, updatedFinancialData.getBalance());
    }

    @Test
    void updatePassengerBalance_ShouldThrowInsufficientBalanceException() {
        Passenger passenger = new Passenger();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPhoneNumber("1234567890");
        passengerRepository.save(passenger);

        FinancialData financialData = new FinancialData();
        financialData.setPassenger(passenger);
        financialData.setBalance(100.0);
        financialDataRepository.save(financialData);

        assertThrows(InsufficientBalanceException.class, () -> {
            passengerService.updatePassengerBalance(passenger.getId(), 150.0);
        });
    }

    @Test
    void updatePassengerBalance_ShouldThrowFinancialDataNotFoundException() {
        Passenger passenger = new Passenger();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPhoneNumber("1234567890");
        passengerRepository.save(passenger);

        assertThrows(FinancialDataNotFoundException.class, () -> {
            passengerService.updatePassengerBalance(passenger.getId(), 50.0);
        });
    }
}