package rides_service.rides_service.service.api;



import rides_service.rides_service.dto.ride.RideRequestDto;
import rides_service.rides_service.dto.ride.RideResponseDto;

import java.util.List;

public interface RideService {
    RideResponseDto createRide(RideRequestDto rideRequestDto);
    RideResponseDto getRideById(Long id);
    List<RideResponseDto> getAllRides();
    RideResponseDto updateRide(Long id, RideRequestDto rideRequestDto);
    void deleteRide(Long id);
}