package rides_service.rides_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rides_service.rides_service.config.mapper.DtoMapper;

import rides_service.rides_service.dto.ride.RideListResponseDto;
import rides_service.rides_service.dto.ride.RideRequestDto;
import rides_service.rides_service.dto.ride.RideResponseDto;
import rides_service.rides_service.entity.Ride;
import rides_service.rides_service.entity.Route;
import rides_service.rides_service.exception.ride.RideNotFoundException;
import rides_service.rides_service.exception.route.RouteNotFoundException;
import rides_service.rides_service.repository.RideRepository;
import rides_service.rides_service.repository.RouteRepository;
import rides_service.rides_service.service.api.RideService;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RouteRepository routeRepository;
    private final DtoMapper mapper;

    @Override
    public RideResponseDto createRide(RideRequestDto rideRequestDto) {
        Route route = routeRepository.findById(rideRequestDto.getRouteId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        Ride ride = new Ride();
        ride.setDriverId(rideRequestDto.getDriverId());
        ride.setPassengerId(rideRequestDto.getPassengerId());
        ride.setRoute(route);
        ride.setStartTime(rideRequestDto.getStartTime());
        ride.setEndTime(rideRequestDto.getEndTime());
        ride.setStatus(rideRequestDto.getStatus());

        Ride savedRide = rideRepository.save(ride);
        return mapper.convertToDto(savedRide, RideResponseDto.class);
    }

    @Override
    public RideResponseDto getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));
        return mapper.convertToDto(ride, RideResponseDto.class);
    }

    @Override
    public RideListResponseDto getAllRides() {
        List<Ride> rides = rideRepository.findAll();
        List<RideResponseDto> rideResponseDtos = rides.stream()
                .map(ride -> mapper.convertToDto(ride, RideResponseDto.class))
                .collect(Collectors.toList());

        return RideListResponseDto.builder()
                .ride(rideResponseDtos)
                .build();
    }

    @Override
    public RideResponseDto updateRide(Long id, RideRequestDto rideRequestDto) {
        Ride existingRide = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));

        Route route = routeRepository.findById(rideRequestDto.getRouteId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        existingRide.setDriverId(rideRequestDto.getDriverId());
        existingRide.setPassengerId(rideRequestDto.getPassengerId());
        existingRide.setRoute(route);
        existingRide.setStartTime(rideRequestDto.getStartTime());
        existingRide.setEndTime(rideRequestDto.getEndTime());
        existingRide.setStatus(rideRequestDto.getStatus());

        Ride updatedRide = rideRepository.save(existingRide);
        return mapper.convertToDto(updatedRide, RideResponseDto.class);
    }

    @Override
    public void deleteRide(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));
        rideRepository.delete(ride);
    }
}
