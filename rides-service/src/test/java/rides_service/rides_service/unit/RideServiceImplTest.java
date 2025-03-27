package rides_service.rides_service.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import rides_service.rides_service.service.impl.RideServiceImpl;
import rides_service.rides_service.service.kafka.KafkaProducerService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceImplTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverServiceClient driverServiceClient;

    @Mock
    private PassengerServiceClient passengerServiceClient;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private RideServiceImpl rideService;

    @Mock
    private DtoMapper mapper;

    @Test
    void getRideById_ShouldReturnRideResponseDto_WhenRideExists() {
        Long rideId = 1L;
        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setDriverId(1L);
        ride.setPassengerId(1L);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverServiceClient.getDriverById(1L)).thenReturn(new DriverResponseDto());
        when(passengerServiceClient.getPassengerById(1L)).thenReturn(new PassengerResponseDto());
        when(paymentServiceClient.getPaymentByRideId(rideId)).thenReturn(new PaymentResponseDto());
        when(mapper.convertToDto(any(Ride.class), eq(RideResponseDto.class))).thenReturn(new RideResponseDto());

        RideResponseDto result = rideService.getRideById(rideId);

        assertNotNull(result);
        verify(rideRepository, times(1)).findById(rideId);
        verify(mapper, times(1)).convertToDto(any(Ride.class), eq(RideResponseDto.class));
    }

    @Test
    void getRideById_ShouldThrowRideNotFoundException_WhenRideNotFound() {
        Long rideId = 1L;
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.getRideById(rideId));
        verify(rideRepository, times(1)).findById(rideId);
    }

    @Test
    void createRide_ShouldCreateRide_WhenValidRequest() {
        RideRequestDto rideRequestDto = new RideRequestDto();
        rideRequestDto.setDriverId(1L);
        rideRequestDto.setPassengerId(1L);
        rideRequestDto.setRouteId(1L);
        rideRequestDto.setPaymentMethod("CARD");

        Route route = new Route();
        route.setId(1L);
        route.setDistance(10.0f);

        Ride ride = new Ride();
        ride.setId(1L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);
        when(paymentServiceClient.createPayment(any(PaymentRequestDto.class))).thenReturn(new PaymentResponseDto());
        when(mapper.convertToDto(any(Ride.class), eq(RideResponseDto.class))).thenReturn(new RideResponseDto());

        doNothing().when(kafkaProducerService).sendAvailableRide(anyString());

        RideResponseDto result = rideService.createRide(rideRequestDto);

        assertNotNull(result);
        verify(rideRepository, times(1)).save(any(Ride.class));
        verify(paymentServiceClient, times(1)).createPayment(any(PaymentRequestDto.class));
        verify(kafkaProducerService, times(1)).sendAvailableRide(anyString());
    }

    @Test
    void updateRideStatus_ShouldUpdateStatus_WhenValidStatus() {
        Long rideId = 1L;
        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setStatus("PENDING");

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        rideService.updateRideStatus(rideId, "ACCEPTED");

        assertEquals("ACCEPTED", ride.getStatus());
        verify(rideRepository, times(1)).save(ride);
    }

    @Test
    void getAllRides_ShouldReturnListOfRides() {
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setDriverId(1L);
        ride.setPassengerId(1L);

        List<Ride> rides = Collections.singletonList(ride);
        when(rideRepository.findAll()).thenReturn(rides);
        when(driverServiceClient.getDriverById(1L)).thenReturn(new DriverResponseDto());
        when(passengerServiceClient.getPassengerById(1L)).thenReturn(new PassengerResponseDto());
        when(paymentServiceClient.getPaymentByRideId(1L)).thenReturn(new PaymentResponseDto());
        when(mapper.convertToDto(any(Ride.class), eq(RideResponseDto.class))).thenReturn(new RideResponseDto());

        RideListResponseDto result = rideService.getAllRides();

        assertNotNull(result);
        assertEquals(1, result.getRide().size());
        verify(rideRepository, times(1)).findAll();
        verify(mapper, times(1)).convertToDto(any(Ride.class), eq(RideResponseDto.class));
    }

    @Test
    void updateRide_ShouldUpdateRide_WhenValidRequest() {
        Long rideId = 1L;
        RideRequestDto rideRequestDto = new RideRequestDto();
        rideRequestDto.setDriverId(1L);
        rideRequestDto.setPassengerId(1L);
        rideRequestDto.setRouteId(1L);
        rideRequestDto.setPaymentMethod("CARD");

        Ride existingRide = new Ride();
        existingRide.setId(rideId);

        Route route = new Route();
        route.setId(1L);
        route.setDistance(10.0f);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(rideRepository.save(any(Ride.class))).thenReturn(existingRide);
        when(driverServiceClient.getDriverById(1L)).thenReturn(new DriverResponseDto());
        when(passengerServiceClient.getPassengerById(1L)).thenReturn(new PassengerResponseDto());
        when(mapper.convertToDto(any(Ride.class), eq(RideResponseDto.class))).thenReturn(new RideResponseDto());

        RideResponseDto result = rideService.updateRide(rideId, rideRequestDto);

        assertNotNull(result);
        verify(rideRepository, times(1)).findById(rideId);
        verify(rideRepository, times(1)).save(any(Ride.class));
        verify(mapper, times(1)).convertToDto(any(Ride.class), eq(RideResponseDto.class));
    }

    @Test
    void deleteRide_ShouldDeleteRide_WhenRideExists() {
        Long rideId = 1L;
        Ride ride = new Ride();
        ride.setId(rideId);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        doNothing().when(rideRepository).delete(ride);

        rideService.deleteRide(rideId);

        verify(rideRepository, times(1)).findById(rideId);
        verify(rideRepository, times(1)).delete(ride);
    }
}