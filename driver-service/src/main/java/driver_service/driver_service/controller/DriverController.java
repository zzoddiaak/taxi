package driver_service.driver_service.controller;

import driver_service.driver_service.dto.driver.DriverListResponseDto;
import driver_service.driver_service.dto.driver.DriverRequestDto;
import driver_service.driver_service.dto.driver.DriverResponseDto;
import driver_service.driver_service.dto.rating.RatingUpdateDto;
import driver_service.driver_service.service.api.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;

    @PutMapping("/{id}/rating")
    public ResponseEntity<Void> updateDriverRating(@PathVariable Long id, @RequestBody RatingUpdateDto ratingUpdateDto) {
        service.updateDriverRating(id, ratingUpdateDto.getRating());
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<DriverResponseDto> createDriver(@RequestBody DriverRequestDto driverRequestDto){
        DriverResponseDto driverResponseDto = service.createDriver(driverRequestDto);
        return new ResponseEntity<>(driverResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDto> getDriverById(@PathVariable Long id){
        DriverResponseDto driverResponseDto = service.getDriverById(id);
        return ResponseEntity.ok(driverResponseDto);
    }

    @GetMapping
    public ResponseEntity<DriverListResponseDto> getAllDrivers(){
        DriverListResponseDto driverListResponseDto = service.getAllDrivers();
        return ResponseEntity.ok(driverListResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDto> updateDriver(@PathVariable Long id, @RequestBody DriverRequestDto driverRequestDto){
        DriverResponseDto driverResponseDto = service.updateDriver(id, driverRequestDto);
        return ResponseEntity.ok(driverResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id){
        service.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}