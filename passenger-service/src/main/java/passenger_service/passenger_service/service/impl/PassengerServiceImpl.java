package passenger_service.passenger_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import passenger_service.passenger_service.config.mapper.DtoMapper;
import passenger_service.passenger_service.dto.PassengerListResponseDto;
import passenger_service.passenger_service.dto.PassengerRequestDto;
import passenger_service.passenger_service.dto.PassengerResponseDto;
import passenger_service.passenger_service.entity.FinancialData;
import passenger_service.passenger_service.entity.Passenger;
import passenger_service.passenger_service.exception.passenger.PassengerNotFoundException;
import passenger_service.passenger_service.repository.FinancialDataRepository;
import passenger_service.passenger_service.repository.PassengerRepository;
import passenger_service.passenger_service.service.api.PassengerService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final FinancialDataRepository financialDataRepository;
    private final DtoMapper mapper;

    @Override
    public PassengerResponseDto createPassenger(PassengerRequestDto passengerRequestDto) {
        Passenger passenger = mapper.convertToPassengerEntity(passengerRequestDto);
        passenger.setAverageRating(0.0);
        passenger.setRatingCount(0);
        passenger.setDriverRating(passengerRequestDto.getDriverRating());
        Passenger savedPassenger = passengerRepository.save(passenger);

        if (savedPassenger.getFinancialData() != null) {
            financialDataRepository.save(savedPassenger.getFinancialData());
        }

        return mapper.convertToPassengerDto(savedPassenger);
    }

    @Override
    public PassengerResponseDto updatePassenger(Long id, PassengerRequestDto passengerRequestDto) {
        Passenger existingPassenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id: " + id));

        existingPassenger.setFirstName(passengerRequestDto.getFirstName());
        existingPassenger.setLastName(passengerRequestDto.getLastName());
        existingPassenger.setEmail(passengerRequestDto.getEmail());
        existingPassenger.setPhoneNumber(passengerRequestDto.getPhoneNumber());
        existingPassenger.setDriverRating(passengerRequestDto.getDriverRating());

        if (existingPassenger.getFinancialData() != null) {
            FinancialData financialData = existingPassenger.getFinancialData();
            financialData.setBalance(passengerRequestDto.getBalance());
            financialData.setCardNumber(passengerRequestDto.getCardNumber());
            financialData.setCardExpiryDate(passengerRequestDto.getCardExpiryDate());
            financialData.setCardCvv(passengerRequestDto.getCardCvv());
            financialData.setPromo(passengerRequestDto.getPromo());
        } else {
            FinancialData financialData = new FinancialData(null, existingPassenger, passengerRequestDto.getBalance(), passengerRequestDto.getCardNumber(), passengerRequestDto.getCardExpiryDate(), passengerRequestDto.getCardCvv(), passengerRequestDto.getPromo());
            existingPassenger.setFinancialData(financialData);
        }

        Passenger updatedPassenger = passengerRepository.save(existingPassenger);
        financialDataRepository.save(existingPassenger.getFinancialData());

        return mapper.convertToPassengerDto(updatedPassenger);
    }

    @Override
    public PassengerResponseDto getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id: " + id));
        return mapper.convertToPassengerDto(passenger);
    }

    @Override
    public PassengerListResponseDto getAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAll();
        List<PassengerResponseDto> passengerResponseDtos = passengers.stream()
                .map(mapper::convertToPassengerDto)
                .toList();

        return PassengerListResponseDto.builder()
                .passengers(passengerResponseDtos)
                .build();
    }

    @Override
    public void deletePassenger(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException(String.format("Passenger not found with id: " + id)));
        passengerRepository.delete(passenger);
    }
}
