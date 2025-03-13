package driver_service.driver_service.controller;

import driver_service.driver_service.dto.driver.DriverListResponseDto;
import driver_service.driver_service.dto.driver.DriverRequestDto;
import driver_service.driver_service.dto.driver.DriverResponseDto;
import driver_service.driver_service.dto.payment.PaymentStatusUpdateDto;
import driver_service.driver_service.dto.rating.RatingUpdateDto;
import driver_service.driver_service.service.api.DriverService;
import driver_service.driver_service.service.api.PaymentServiceClient;
import driver_service.driver_service.service.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;
    private final PaymentServiceClient paymentServiceClient;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaProducerService driverRatingService;


    @PostMapping("/{driverId}/rate-passenger/{passengerId}")
    public ResponseEntity<Void> ratePassenger(
            @PathVariable Long driverId,
            @PathVariable Long passengerId,
            @RequestParam Float rating) {

        driverRatingService.ratePassenger(driverId, passengerId, rating);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{driverId}/start-ride/{rideId}")
    public ResponseEntity<Void> startRide(@PathVariable Long driverId, @PathVariable Long rideId) {
        kafkaProducerService.sendRideStart(rideId.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{driverId}/end-ride/{rideId}")
    public ResponseEntity<Void> endRide(@PathVariable Long driverId, @PathVariable Long rideId) {
        kafkaProducerService.sendRideEnd(rideId.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{driverId}/accept-ride/{rideId}")
    public ResponseEntity<Void> acceptRide(@PathVariable Long driverId, @PathVariable Long rideId, @RequestParam boolean accepted) {
        kafkaProducerService.sendRideAcceptance(rideId.toString(), driverId.toString(), accepted);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/payments/{paymentId}/status")
    public ResponseEntity<Void> updatePaymentStatus(@PathVariable Long paymentId, @RequestBody PaymentStatusUpdateDto paymentStatusUpdateDto) {
        paymentServiceClient.updatePaymentStatus(paymentId, paymentStatusUpdateDto);
        return ResponseEntity.ok().build();
    }

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