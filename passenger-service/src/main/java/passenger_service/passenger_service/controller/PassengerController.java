package passenger_service.passenger_service.controller;

import lombok.RequiredArgsConstructor;
import passenger_service.passenger_service.dto.PassengerListResponseDto;
import passenger_service.passenger_service.dto.PassengerRequestDto;
import passenger_service.passenger_service.dto.PassengerResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import passenger_service.passenger_service.service.api.PassengerService;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

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