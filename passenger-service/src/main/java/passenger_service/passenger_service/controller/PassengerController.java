package passenger_service.passenger_service.controller;

import lombok.RequiredArgsConstructor;
import passenger_service.passenger_service.dto.financial.BalanceUpdateDto;
import passenger_service.passenger_service.dto.passenger.PassengerListResponseDto;
import passenger_service.passenger_service.dto.passenger.PassengerRequestDto;
import passenger_service.passenger_service.dto.passenger.PassengerResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import passenger_service.passenger_service.dto.rating.RatingUpdateDto;
import passenger_service.passenger_service.service.api.PassengerService;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @PutMapping("/{id}/balance")
    public ResponseEntity<Void> updatePassengerBalance(@PathVariable Long id, @RequestBody BalanceUpdateDto balanceUpdateDto) {
        passengerService.updatePassengerBalance(id, balanceUpdateDto.getAmount());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/rating")
    public ResponseEntity<Void> updatePassengerRating(@PathVariable Long id, @RequestBody RatingUpdateDto ratingUpdateDto) {
        passengerService.updatePassengerRating(id, ratingUpdateDto.getRating());
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<PassengerResponseDto> createPassenger(@RequestBody PassengerRequestDto passengerRequestDto) {
        PassengerResponseDto passengerResponseDto = passengerService.createPassenger(passengerRequestDto);
        return new ResponseEntity<>(passengerResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDto> getPassengerById(@PathVariable Long id) {
        PassengerResponseDto passengerResponseDto = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @GetMapping
    public ResponseEntity<PassengerListResponseDto> getAllPassengers() {
        PassengerListResponseDto passengers = passengerService.getAllPassengers();
        return ResponseEntity.ok(passengers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerResponseDto> updatePassenger(@PathVariable Long id, @RequestBody PassengerRequestDto passengerRequestDto) {
        PassengerResponseDto passengerResponseDto = passengerService.updatePassenger(id, passengerRequestDto);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }
}