package rides_service.rides_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rides_service.rides_service.config.mapper.DtoMapper;
import rides_service.rides_service.dto.driver.DriverResponseDto;
import rides_service.rides_service.dto.passenger.PassengerResponseDto;
import rides_service.rides_service.dto.payment.PaymentRequestDto;
import rides_service.rides_service.dto.payment.PaymentResponseDto;

import rides_service.rides_service.dto.ride.RideListResponseDto;
import rides_service.rides_service.dto.ride.RideRequestDto;
import rides_service.rides_service.dto.ride.RideResponseDto;
import rides_service.rides_service.entity.Ride;
import rides_service.rides_service.entity.Route;
import rides_service.rides_service.exception.ride.RideNotFoundException;
import rides_service.rides_service.exception.route.RouteNotFoundException;
import rides_service.rides_service.repository.RideRepository;
import rides_service.rides_service.repository.RouteRepository;
import rides_service.rides_service.service.api.DriverServiceClient;
import rides_service.rides_service.service.api.PassengerServiceClient;
import rides_service.rides_service.service.api.PaymentServiceClient;
import rides_service.rides_service.service.api.RideService;
import rides_service.rides_service.service.kafka.KafkaProducerService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RideServiceImpl implements RideService {

    private static final BigDecimal PRICE_PER_KM = new BigDecimal("10.00");
    private final RideRepository rideRepository;
    private final RouteRepository routeRepository;
    private final DtoMapper mapper;
    private final DriverServiceClient driverServiceClient;
    private final PassengerServiceClient passengerServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public RideListResponseDto getAllRides() {
        log.debug("Fetching all rides");
        List<Ride> rides = rideRepository.findAll();
        log.info("Processing {} rides", rides.size());

        List<RideResponseDto> rideResponseDtos = rides.stream()
                .map(ride -> {
                    log.debug("Processing ride ID: {}", ride.getId());
                    DriverResponseDto driverResponseDto = driverServiceClient.getDriverById(ride.getDriverId());
                    PassengerResponseDto passengerResponseDto = passengerServiceClient.getPassengerById(ride.getPassengerId());
                    PaymentResponseDto paymentResponseDto = paymentServiceClient.getPaymentByRideId(ride.getId());

                    RideResponseDto rideResponseDto = mapper.convertToDto(ride, RideResponseDto.class);
                    rideResponseDto.setDriver(driverResponseDto);
                    rideResponseDto.setPassenger(passengerResponseDto);
                    rideResponseDto.setAmount(paymentResponseDto);
                    return rideResponseDto;
                })
                .collect(Collectors.toList());


        return RideListResponseDto.builder()
                .ride(rideResponseDtos)
                .build();
    }

    @Override
    public RideResponseDto getRideById(Long id) {
        log.info("Fetching ride by ID: {}", id);
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ride not found: id={}", id);
                    return new RideNotFoundException("Ride not found with id: " + id);
                });

        log.debug("Fetching driver and passenger info for ride ID: {}", id);
        DriverResponseDto driverResponseDto = driverServiceClient.getDriverById(ride.getDriverId());
        PassengerResponseDto passengerResponseDto = passengerServiceClient.getPassengerById(ride.getPassengerId());
        PaymentResponseDto paymentResponseDto = paymentServiceClient.getPaymentByRideId(id);

        RideResponseDto rideResponseDto = mapper.convertToDto(ride, RideResponseDto.class);
        rideResponseDto.setDriver(driverResponseDto);
        rideResponseDto.setPassenger(passengerResponseDto);
        rideResponseDto.setAmount(paymentResponseDto);
        return rideResponseDto;
    }

    @Override
    public RideResponseDto updateRide(Long id, RideRequestDto rideRequestDto) {
        log.info("Updating ride ID {}: {}", id, rideRequestDto);
        Ride existingRide = rideRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ride not found during update: id={}", id);
                    return new RideNotFoundException("Ride not found with id: " + id);
                });

        log.debug("Fetching route for update");
        Route route = routeRepository.findById(rideRequestDto.getRouteId())
                .orElseThrow(() -> {
                    log.error("Route not found during ride update: routeId={}", rideRequestDto.getRouteId());
                    return new RouteNotFoundException("Route not found");
                });

        DriverResponseDto driverResponseDto = driverServiceClient.getDriverById(rideRequestDto.getDriverId());
        PassengerResponseDto passengerResponseDto = passengerServiceClient.getPassengerById(rideRequestDto.getPassengerId());

        existingRide.setDriverId(rideRequestDto.getDriverId());
        existingRide.setPassengerId(rideRequestDto.getPassengerId());
        existingRide.setRoute(route);
        existingRide.setStartTime(rideRequestDto.getStartTime());
        existingRide.setEndTime(rideRequestDto.getEndTime());
        existingRide.setStatus(rideRequestDto.getStatus());

        if (!existingRide.getRoute().getId().equals(rideRequestDto.getRouteId())) {
            log.debug("Route changed, recalculating cost");
            BigDecimal newAmount = calculateRideCost(route.getDistance());
            existingRide.setAmount(newAmount);
        }

        Ride updatedRide = rideRepository.save(existingRide);
        log.info("Ride updated successfully: {}", updatedRide.getId());
        return mapper.convertToDto(updatedRide, RideResponseDto.class);
    }

    @Override
    public RideResponseDto createRide(RideRequestDto rideRequestDto) {
        log.info("Creating new ride: {}", rideRequestDto);
        Route route = routeRepository.findById(rideRequestDto.getRouteId())
                .orElseThrow(() -> {
                    log.error("Route not found for ride creation: routeId={}", rideRequestDto.getRouteId());
                    return new RouteNotFoundException("Route not found");
                });

        BigDecimal amount = calculateRideCost(route.getDistance());
        log.debug("Calculated ride cost: {}", amount);

        Ride ride = new Ride();
        ride.setDriverId(rideRequestDto.getDriverId());
        ride.setPassengerId(rideRequestDto.getPassengerId());
        ride.setRoute(route);
        ride.setStartTime(null);
        ride.setEndTime(null);
        ride.setStatus("PENDING");
        ride.setAmount(amount);

        Ride savedRide = rideRepository.save(ride);
        log.info("Ride created with ID: {}", savedRide.getId());

        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .rideId(savedRide.getId())
                .passengerId(rideRequestDto.getPassengerId())
                .amount(amount)
                .paymentMethod(rideRequestDto.getPaymentMethod())
                .status("pending")
                .promoCode(rideRequestDto.getPromoCode())
                .build();

        log.debug("Creating payment for ride ID: {}", savedRide.getId());
        PaymentResponseDto paymentResponseDto = paymentServiceClient.createPayment(paymentRequestDto);
        kafkaProducerService.sendAvailableRide(savedRide.getId().toString());
        log.info("Kafka notification sent for new ride");

        RideResponseDto rideResponseDto = mapper.convertToDto(savedRide, RideResponseDto.class);
        rideResponseDto.setAmount(paymentResponseDto);
        return rideResponseDto;
    }

    @Override
    public void updateRideStatus(Long rideId, String status) {
        log.info("Updating ride status: rideId={}, newStatus={}", rideId, status);
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> {
                    log.error("Ride not found during status update: id={}", rideId);
                    return new RideNotFoundException("Ride not found with id: " + rideId);
                });

        if ("ACCEPTED".equals(status)) {
            log.debug("Setting ride status to ACCEPTED");
            ride.setStatus("ACCEPTED");
        } else if ("IN_PROGRESS".equals(status)) {
            log.debug("Starting ride, setting start time");
            ride.setStartTime(LocalDateTime.now());
            ride.setStatus("IN_PROGRESS");
        } else if ("COMPLETED".equals(status)) {
            log.debug("Completing ride, setting end time");
            ride.setEndTime(LocalDateTime.now());
            ride.setStatus("COMPLETED");
            kafkaProducerService.sendRideCompleted(rideId.toString(), ride.getPassengerId().toString(), ride.getAmount().toString());
            log.info("Kafka notification sent for completed ride");
        } else if ("DECLINED".equals(status)) {
            log.debug("Setting ride status to DECLINED");
            ride.setStatus("DECLINED");
        }

        rideRepository.save(ride);
        log.debug("Ride status updated successfully");
    }

    private BigDecimal calculateRideCost(Float distance) {
        log.trace("Calculating cost for distance: {}", distance);
        return PRICE_PER_KM.multiply(BigDecimal.valueOf(distance));

    }


    @Override
    public void deleteRide(Long id) {
        log.info("Deleting ride ID: {}", id);
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ride not found during deletion: id={}", id);
                    return new RideNotFoundException("Ride not found with id: " + id);
                });
        rideRepository.delete(ride);
        log.debug("Ride deleted successfully");
    }
}