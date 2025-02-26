package passenger_service.passenger_service.service.api;

import passenger_service.passenger_service.dto.PassengerListResponseDto;
import passenger_service.passenger_service.dto.PassengerRequestDto;
import passenger_service.passenger_service.dto.PassengerResponseDto;

import java.util.List;

public interface PassengerService {

    PassengerResponseDto createPassenger(PassengerRequestDto passengerRequestDto);
    PassengerResponseDto getPassengerById(Long id);
    PassengerListResponseDto getAllPassengers();
    PassengerResponseDto updatePassenger(Long id, PassengerRequestDto passengerRequestDto);
    void deletePassenger(Long id);

}