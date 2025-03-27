package passenger_service.passenger_service.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import passenger_service.passenger_service.config.mapper.DtoMapper;
import passenger_service.passenger_service.dto.passenger.PassengerListResponseDto;
import passenger_service.passenger_service.dto.passenger.PassengerRequestDto;
import passenger_service.passenger_service.dto.passenger.PassengerResponseDto;
import passenger_service.passenger_service.entity.FinancialData;
import passenger_service.passenger_service.entity.Passenger;
import passenger_service.passenger_service.exception.passenger.FinancialDataNotFoundException;
import passenger_service.passenger_service.exception.passenger.InsufficientBalanceException;
import passenger_service.passenger_service.exception.passenger.PassengerNotFoundException;
import passenger_service.passenger_service.repository.FinancialDataRepository;
import passenger_service.passenger_service.repository.PassengerRepository;
import passenger_service.passenger_service.service.impl.PassengerServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private FinancialDataRepository financialDataRepository;

    @Mock
    private DtoMapper mapper;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    @Test
    void updatePassengerRating_ShouldUpdateRating_WhenPassengerExists() {
        Long passengerId = 1L;
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);
        passenger.setAverageRating(4.0);
        passenger.setRatingCount(1);

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));

        passengerService.updatePassengerRating(passengerId, 5.0f);

        assertEquals(4.5, passenger.getAverageRating());
        assertEquals(2, passenger.getRatingCount());
        verify(passengerRepository, times(1)).save(passenger);
    }

    @Test
    void updatePassengerRating_ShouldThrowException_WhenPassengerNotFound() {
        Long passengerId = 1L;
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.empty());

        assertThrows(PassengerNotFoundException.class, () -> passengerService.updatePassengerRating(passengerId, 5.0f));
    }

    @Test
    void createPassenger_ShouldCreatePassenger_WhenValidRequest() {
        PassengerRequestDto requestDto = new PassengerRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        Passenger passenger = new Passenger();
        passenger.setId(1L);
        passenger.setFirstName("John");
        passenger.setLastName("Doe");

        when(mapper.convertToPassengerEntity(requestDto)).thenReturn(passenger);
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        when(mapper.convertToPassengerDto(passenger)).thenReturn(new PassengerResponseDto());

        PassengerResponseDto result = passengerService.createPassenger(requestDto);

        assertNotNull(result);
        verify(passengerRepository, times(1)).save(passenger);
    }

    @Test
    void updatePassenger_ShouldUpdatePassenger_WhenValidRequest() {
        Long passengerId = 1L;
        PassengerRequestDto requestDto = new PassengerRequestDto();
        requestDto.setFirstName("Updated John");
        requestDto.setLastName("Updated Doe");

        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(passengerId);
        existingPassenger.setFirstName("John");
        existingPassenger.setLastName("Doe");

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(existingPassenger));
        when(passengerRepository.save(existingPassenger)).thenReturn(existingPassenger);
        when(mapper.convertToPassengerDto(existingPassenger)).thenReturn(new PassengerResponseDto());

        PassengerResponseDto result = passengerService.updatePassenger(passengerId, requestDto);

        assertNotNull(result);
        assertEquals("Updated John", existingPassenger.getFirstName());
        assertEquals("Updated Doe", existingPassenger.getLastName());
        verify(passengerRepository, times(1)).save(existingPassenger);
    }

    @Test
    void getPassengerById_ShouldReturnPassenger_WhenPassengerExists() {
        Long passengerId = 1L;
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));
        when(mapper.convertToPassengerDto(passenger)).thenReturn(new PassengerResponseDto());

        PassengerResponseDto result = passengerService.getPassengerById(passengerId);

        assertNotNull(result);
        verify(passengerRepository, times(1)).findById(passengerId);
    }

    @Test
    void getPassengerById_ShouldThrowException_WhenPassengerNotFound() {
        Long passengerId = 1L;
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.empty());

        assertThrows(PassengerNotFoundException.class, () -> passengerService.getPassengerById(passengerId));
    }

    @Test
    void getAllPassengers_ShouldReturnListOfPassengers() {
        Passenger passenger = new Passenger();
        passenger.setId(1L);

        when(passengerRepository.findAll()).thenReturn(List.of(passenger));
        when(mapper.convertToPassengerDto(passenger)).thenReturn(new PassengerResponseDto());

        PassengerListResponseDto result = passengerService.getAllPassengers();

        assertNotNull(result);
        assertEquals(1, result.getPassengers().size());
        verify(passengerRepository, times(1)).findAll();
    }

    @Test
    void deletePassenger_ShouldDeletePassenger_WhenPassengerExists() {
        Long passengerId = 1L;
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));
        doNothing().when(passengerRepository).delete(passenger);

        passengerService.deletePassenger(passengerId);

        verify(passengerRepository, times(1)).delete(passenger);
    }

    @Test
    void updatePassengerBalance_ShouldUpdateBalance_WhenSufficientFunds() {
        Long passengerId = 1L;
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);

        FinancialData financialData = new FinancialData();
        financialData.setBalance(100.0);

        passenger.setFinancialData(financialData);

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));

        passengerService.updatePassengerBalance(passengerId, 50.0);

        assertEquals(50.0, financialData.getBalance());
        verify(financialDataRepository, times(1)).save(financialData);
    }

    @Test
    void updatePassengerBalance_ShouldThrowException_WhenFinancialDataNotFound() {
        Long passengerId = 1L;
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));

        assertThrows(FinancialDataNotFoundException.class, () -> passengerService.updatePassengerBalance(passengerId, 50.0));
    }

    @Test
    void updatePassengerBalance_ShouldThrowException_WhenInsufficientFunds() {
        Long passengerId = 1L;
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);

        FinancialData financialData = new FinancialData();
        financialData.setBalance(30.0);

        passenger.setFinancialData(financialData);

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));

        assertThrows(InsufficientBalanceException.class, () -> passengerService.updatePassengerBalance(passengerId, 50.0));
    }
}