package passenger_service.passenger_service.controller;

import lombok.RequiredArgsConstructor;
import passenger_service.passenger_service.dto.financial.BalanceUpdateDto;
import passenger_service.passenger_service.dto.passenger.PassengerListResponseDto;
import passenger_service.passenger_service.dto.passenger.PassengerRequestDto;
import passenger_service.passenger_service.dto.passenger.PassengerResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import passenger_service.passenger_service.dto.rating.PassengerRatingRequestDto;
import passenger_service.passenger_service.dto.rating.RatingUpdateDto;
import passenger_service.passenger_service.service.api.PassengerService;
import passenger_service.passenger_service.service.kafka.KafkaProducerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
@Tag(name = "Passenger Controller", description = "API for managing passengers")
public class PassengerController {

    private final PassengerService passengerService;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/{passengerId}/rate-driver/{driverId}")
    public ResponseEntity<Void> rateDriver(
            @PathVariable Long passengerId,
            @PathVariable Long driverId,
            @RequestParam Float rating) {

        kafkaProducerService.rateDriver(passengerId, driverId, rating);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/balance")
    @Operation(summary = "Update passenger balance", description = "Updates the balance of a passenger")
    public ResponseEntity<Void> updatePassengerBalance(@PathVariable Long id, @RequestBody BalanceUpdateDto balanceUpdateDto) {
        passengerService.updatePassengerBalance(id, balanceUpdateDto.getAmount());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/rating")
    @Operation(summary = "Update passenger rating", description = "Updates the rating of a passenger")
    public ResponseEntity<Void> updatePassengerRating(@PathVariable Long id, @RequestBody RatingUpdateDto ratingUpdateDto) {
        passengerService.updatePassengerRating(id, ratingUpdateDto.getRating());
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @Operation(summary = "Create a passenger", description = "Creates a new passenger")
    public ResponseEntity<PassengerResponseDto> createPassenger(@RequestBody PassengerRequestDto passengerRequestDto) {
        PassengerResponseDto passengerResponseDto = passengerService.createPassenger(passengerRequestDto);
        return new ResponseEntity<>(passengerResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get passenger by ID", description = "Retrieves a passenger by their ID")
    public ResponseEntity<PassengerResponseDto> getPassengerById(@PathVariable Long id) {
        PassengerResponseDto passengerResponseDto = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @GetMapping
    @Operation(summary = "Get all passengers", description = "Retrieves a list of all passengers")
    public ResponseEntity<PassengerListResponseDto> getAllPassengers() {
        PassengerListResponseDto passengers = passengerService.getAllPassengers();
        return ResponseEntity.ok(passengers);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update passenger", description = "Updates an existing passenger")
    public ResponseEntity<PassengerResponseDto> updatePassenger(@PathVariable Long id, @RequestBody PassengerRequestDto passengerRequestDto) {
        PassengerResponseDto passengerResponseDto = passengerService.updatePassenger(id, passengerRequestDto);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete passenger", description = "Deletes a passenger by their ID")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }
}