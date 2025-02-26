package rides_service.rides_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rides_service.rides_service.config.mapper.DtoMapper;

import rides_service.rides_service.dto.ride.RideListResponseDto;
import rides_service.rides_service.dto.ride.RideRequestDto;
import rides_service.rides_service.dto.ride.RideResponseDto;
import rides_service.rides_service.entity.Ride;
import rides_service.rides_service.exception.ride.RideNotFoundException;
import rides_service.rides_service.repository.RideRepository;
import rides_service.rides_service.service.api.RideService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final DtoMapper mapper;

    @Override
    public RideResponseDto createRide(RideRequestDto rideRequestDto) {
        Ride ride = mapper.convertToEntity(rideRequestDto, Ride.class);
        Ride savedRide = rideRepository.save(ride);
        return mapper.convertToDto(savedRide, RideResponseDto.class);
    }

    @Override
    public RideResponseDto getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(String.format("Ride not found with id: " + id)));
        return mapper.convertToDto(ride, RideResponseDto.class);
    }

    @Override
    public RideListResponseDto getAllRides() {
        List<Ride> rides = rideRepository.findAll();
        List<RideResponseDto> rideResponseDtos = rides.stream()
                .map(payment -> mapper.convertToDto(payment, RideResponseDto.class))
                .toList();

        return RideListResponseDto.builder()
                .ride(rideResponseDtos)
                .build();
    }

    @Override
    public RideResponseDto updateRide(Long id, RideRequestDto rideRequestDto) {
        Ride existingRide = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(String.format("Ride not found with id: " + id)));

        existingRide.setDriverId(rideRequestDto.getDriverId());
        existingRide.setPassengerId(rideRequestDto.getPassengerId());
        existingRide.setStartAddress(rideRequestDto.getStartAddress());
        existingRide.setEndAddress(rideRequestDto.getEndAddress());
        existingRide.setStartTime(rideRequestDto.getStartTime());
        existingRide.setEndTime(rideRequestDto.getEndTime());
        existingRide.setStatus(rideRequestDto.getStatus());
        existingRide.setDistance(rideRequestDto.getDistance());
        existingRide.setEstimatedTime(rideRequestDto.getEstimatedTime());

        Ride updatedRide = rideRepository.save(existingRide);
        return mapper.convertToDto(updatedRide, RideResponseDto.class);
    }

    @Override
    public void deleteRide(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(String.format("Ride not found with id: " + id)));
        rideRepository.delete(ride);
    }
}