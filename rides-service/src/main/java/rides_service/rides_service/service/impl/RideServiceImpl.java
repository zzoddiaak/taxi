package rides_service.rides_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RideServiceImpl implements RideService {

    private static final BigDecimal PRICE_PER_KM = new BigDecimal("10.00"); // Тариф за километр

    private final RideRepository rideRepository;
    private final RouteRepository routeRepository;
    private final DtoMapper mapper;
    private final DriverServiceClient driverServiceClient;
    private final PassengerServiceClient passengerServiceClient;
    private final PaymentServiceClient paymentServiceClient;



    @Override
    public RideListResponseDto getAllRides() {
        List<Ride> rides = rideRepository.findAll();
        List<RideResponseDto> rideResponseDtos = rides.stream()
                .map(ride -> {
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
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));

        // Получение данных о водителе и пассажире через Feign-клиенты
        DriverResponseDto driverResponseDto = driverServiceClient.getDriverById(ride.getDriverId());
        PassengerResponseDto passengerResponseDto = passengerServiceClient.getPassengerById(ride.getPassengerId());

        // Получение данных о платеже через Feign-клиент
        PaymentResponseDto paymentResponseDto = paymentServiceClient.getPaymentByRideId(id);

        // Создание DTO с полными данными о водителе, пассажире и платеже
        RideResponseDto rideResponseDto = mapper.convertToDto(ride, RideResponseDto.class);
        rideResponseDto.setDriver(driverResponseDto);
        rideResponseDto.setPassenger(passengerResponseDto);
        rideResponseDto.setAmount(paymentResponseDto); // Установка данных о платеже

        return rideResponseDto;
    }

    @Override
    public RideResponseDto updateRide(Long id, RideRequestDto rideRequestDto) {
        Ride existingRide = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));

        Route route = routeRepository.findById(rideRequestDto.getRouteId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        DriverResponseDto driverResponseDto = driverServiceClient.getDriverById(rideRequestDto.getDriverId());
        PassengerResponseDto passengerResponseDto = passengerServiceClient.getPassengerById(rideRequestDto.getPassengerId());

        existingRide.setDriverId(rideRequestDto.getDriverId());
        existingRide.setPassengerId(rideRequestDto.getPassengerId());
        existingRide.setRoute(route);
        existingRide.setStartTime(rideRequestDto.getStartTime());
        existingRide.setEndTime(rideRequestDto.getEndTime());
        existingRide.setStatus(rideRequestDto.getStatus());

        if (!existingRide.getRoute().getId().equals(rideRequestDto.getRouteId())) {
            BigDecimal newAmount = calculateRideCost(route.getDistance());
            existingRide.setAmount(newAmount);
        }

        Ride updatedRide = rideRepository.save(existingRide);

        RideResponseDto rideResponseDto = mapper.convertToDto(updatedRide, RideResponseDto.class);
        rideResponseDto.setDriver(driverResponseDto);
        rideResponseDto.setPassenger(passengerResponseDto);

        return rideResponseDto;
    }

    @Override
    public RideResponseDto createRide(RideRequestDto rideRequestDto) {
        Route route = routeRepository.findById(rideRequestDto.getRouteId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        BigDecimal amount = calculateRideCost(route.getDistance());

        Ride ride = new Ride();
        ride.setDriverId(rideRequestDto.getDriverId());
        ride.setPassengerId(rideRequestDto.getPassengerId());
        ride.setRoute(route);
        ride.setStartTime(rideRequestDto.getStartTime());
        ride.setEndTime(rideRequestDto.getEndTime());
        ride.setStatus(rideRequestDto.getStatus());
        ride.setAmount(amount);

        Ride savedRide = rideRepository.save(ride);

        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .rideId(savedRide.getId())
                .passengerId(rideRequestDto.getPassengerId())
                .amount(amount)
                .paymentMethod(rideRequestDto.getPaymentMethod())
                .status("pending")
                .promoCode(rideRequestDto.getPromoCode())
                .build();

        PaymentResponseDto paymentResponseDto = paymentServiceClient.createPayment(paymentRequestDto);

        RideResponseDto rideResponseDto = mapper.convertToDto(savedRide, RideResponseDto.class);
        rideResponseDto.setAmount(paymentResponseDto);

        return rideResponseDto;
    }

    private BigDecimal calculateRideCost(Float distance) {
        return PRICE_PER_KM.multiply(BigDecimal.valueOf(distance));
    }

    @Override
    public void deleteRide(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));
        rideRepository.delete(ride);
    }
}