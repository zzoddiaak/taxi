package rides_service.rides_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rides_service.rides_service.dto.ride.RideListResponseDto;
import rides_service.rides_service.dto.ride.RideRequestDto;
import rides_service.rides_service.dto.ride.RideResponseDto;
import rides_service.rides_service.service.api.RideService;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<RideResponseDto> createRide(@RequestBody RideRequestDto rideRequestDto) {
        RideResponseDto rideResponseDto = rideService.createRide(rideRequestDto);
        return new ResponseEntity<>(rideResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDto> getRideById(@PathVariable Long id) {
        RideResponseDto rideResponseDto = rideService.getRideById(id);
        return ResponseEntity.ok(rideResponseDto);
    }

    @GetMapping
    public ResponseEntity<RideListResponseDto> getAllRides() {
        RideListResponseDto rides = rideService.getAllRides();
        return ResponseEntity.ok(rides);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideResponseDto> updateRide(@PathVariable Long id, @RequestBody RideRequestDto rideRequestDto) {
        RideResponseDto rideResponseDto = rideService.updateRide(id, rideRequestDto);
        return ResponseEntity.ok(rideResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }
}