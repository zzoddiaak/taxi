package passenger_service.passenger_service.service.api;

import passenger_service.passenger_service.dto.passenger.PassengerListResponseDto;
import passenger_service.passenger_service.dto.passenger.PassengerRequestDto;
import passenger_service.passenger_service.dto.passenger.PassengerResponseDto;

public interface PassengerService {

    PassengerResponseDto createPassenger(PassengerRequestDto passengerRequestDto);
    PassengerResponseDto getPassengerById(Long id);
    PassengerListResponseDto getAllPassengers();
    PassengerResponseDto updatePassenger(Long id, PassengerRequestDto passengerRequestDto);
    void deletePassenger(Long id);
    void updatePassengerRating(Long id, Float rating);
    void updatePassengerBalance(Long id, Double amount);

}