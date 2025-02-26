package passenger_service.passenger_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import passenger_service.passenger_service.config.mapper.DtoMapper;
import passenger_service.passenger_service.dto.PassengerRequestDto;
import passenger_service.passenger_service.dto.PassengerResponseDto;
import passenger_service.passenger_service.entity.Passenger;
import passenger_service.passenger_service.repository.PassengerRepository;
import passenger_service.passenger_service.service.api.PassengerService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PassengerServiceImpl implements PassengerService {
    private final PassengerRepository passengerRepository;
    private final DtoMapper mapper;

    @Override
    public PassengerResponseDto createPassenger(PassengerRequestDto passengerRequestDto) {
        Passenger passenger = mapper.convertToEntity(passengerRequestDto, Passenger.class);
        Passenger savedPassenger = passengerRepository.save(passenger);
        return mapper.convertToDto(savedPassenger, PassengerResponseDto.class);
    }

    @Override
    public PassengerResponseDto getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
        return mapper.convertToDto(passenger, PassengerResponseDto.class);
    }

    @Override
    public List<PassengerResponseDto> getAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAll();
        return passengers.stream()
                .map(passenger -> mapper.convertToDto(passenger, PassengerResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PassengerResponseDto updatePassenger(Long id, PassengerRequestDto passengerRequestDto) {
        Passenger existingPassenger = passengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));

        existingPassenger.setFirstName(passengerRequestDto.getFirstName());
        existingPassenger.setLastName(passengerRequestDto.getLastName());
        existingPassenger.setEmail(passengerRequestDto.getEmail());
        existingPassenger.setPhoneNumber(passengerRequestDto.getPhoneNumber());

        Passenger updatedPassenger = passengerRepository.save(existingPassenger);
        return mapper.convertToDto(updatedPassenger, PassengerResponseDto.class);
    }

    @Override
    public void deletePassenger(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
        passengerRepository.delete(passenger);
    }
}
