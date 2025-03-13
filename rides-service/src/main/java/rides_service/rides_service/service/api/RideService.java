package rides_service.rides_service.service.api;



import rides_service.rides_service.dto.ride.RideListResponseDto;
import rides_service.rides_service.dto.ride.RideRequestDto;
import rides_service.rides_service.dto.ride.RideResponseDto;

import java.util.List;

public interface RideService {
    RideResponseDto createRide(RideRequestDto rideRequestDto);
    RideResponseDto getRideById(Long id);
    RideListResponseDto getAllRides();
    RideResponseDto updateRide(Long id, RideRequestDto rideRequestDto);
    void deleteRide(Long id);
    void updateRideStatus(Long rideId, String status);
}