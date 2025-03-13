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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@Tag(name = "Ride Controller", description = "API for managing rides")
public class RideController {

    private final RideService rideService;

    @PostMapping
    @Operation(summary = "Create a ride", description = "Creates a new ride")
    public ResponseEntity<RideResponseDto> createRide(@RequestBody RideRequestDto rideRequestDto) {
        RideResponseDto rideResponseDto = rideService.createRide(rideRequestDto);
        return new ResponseEntity<>(rideResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ride by ID", description = "Retrieves a ride by its ID")
    public ResponseEntity<RideResponseDto> getRideById(@PathVariable Long id) {
        RideResponseDto rideResponseDto = rideService.getRideById(id);
        return ResponseEntity.ok(rideResponseDto);
    }

    @GetMapping
    @Operation(summary = "Get all rides", description = "Retrieves a list of all rides")
    public ResponseEntity<RideListResponseDto> getAllRides() {
        RideListResponseDto rides = rideService.getAllRides();
        return ResponseEntity.ok(rides);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update ride", description = "Updates an existing ride")
    public ResponseEntity<RideResponseDto> updateRide(@PathVariable Long id, @RequestBody RideRequestDto rideRequestDto) {
        RideResponseDto rideResponseDto = rideService.updateRide(id, rideRequestDto);
        return ResponseEntity.ok(rideResponseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ride", description = "Deletes a ride by its ID")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }
}