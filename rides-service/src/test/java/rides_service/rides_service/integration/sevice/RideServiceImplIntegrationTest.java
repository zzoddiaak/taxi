package rides_service.rides_service.integration.sevice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import rides_service.rides_service.dto.driver.DriverResponseDto;
import rides_service.rides_service.dto.passenger.PassengerResponseDto;
import rides_service.rides_service.dto.payment.PaymentRequestDto;
import rides_service.rides_service.dto.payment.PaymentResponseDto;
import rides_service.rides_service.dto.ride.RideListResponseDto;
import rides_service.rides_service.dto.ride.RideRequestDto;
import rides_service.rides_service.dto.ride.RideResponseDto;
import rides_service.rides_service.entity.Ride;
import rides_service.rides_service.entity.Route;
import rides_service.rides_service.repository.RideRepository;
import rides_service.rides_service.repository.RouteRepository;
import rides_service.rides_service.service.api.DriverServiceClient;
import rides_service.rides_service.service.api.PassengerServiceClient;
import rides_service.rides_service.service.api.PaymentServiceClient;
import rides_service.rides_service.service.impl.RideServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@EmbeddedKafka(topics = {"available-rides", "ride-acceptance", "ride-start", "ride-end"})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RideServiceImplIntegrationTest {

    @Autowired
    private RideServiceImpl rideService;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RouteRepository routeRepository;

    @MockBean
    private PaymentServiceClient paymentServiceClient;

    @MockBean
    private DriverServiceClient driverServiceClient;

    @MockBean
    private PassengerServiceClient passengerServiceClient;

    @AfterEach
    void tearDown() {
        rideRepository.deleteAll();
        routeRepository.deleteAll();
    }
    @Test
    void createRide_ShouldCreateRide_WhenValidRequest() {
        Route route = new Route();
        route.setId(1L);
        route.setStartAddress("Start Address");
        route.setEndAddress("End Address");
        route.setDistance(10.0f);
        route.setEstimatedTime(30);
        routeRepository.save(route);

        RideRequestDto rideRequestDto = new RideRequestDto();
        rideRequestDto.setDriverId(1L);
        rideRequestDto.setPassengerId(1L);
        rideRequestDto.setRouteId(1L);
        rideRequestDto.setPaymentMethod("CARD");

        when(paymentServiceClient.createPayment(any(PaymentRequestDto.class))).thenReturn(new PaymentResponseDto());

        RideResponseDto result = rideService.createRide(rideRequestDto);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertNotNull(result.getId());
    }
    @Test
    void getRideById_ShouldReturnRide_WhenRideExists() {
        // Arrange
        Route route = new Route();
        route.setId(1L);
        route.setStartAddress("Start Address");
        route.setEndAddress("End Address");
        route.setDistance(10.0f);
        route.setEstimatedTime(30);
        routeRepository.save(route);

        Ride ride = new Ride();
        ride.setId(1L);
        ride.setDriverId(1L);
        ride.setPassengerId(1L);
        ride.setRoute(route);
        ride.setStatus("PENDING");
        ride.setAmount(BigDecimal.valueOf(100.00));
        rideRepository.save(ride);

        when(driverServiceClient.getDriverById(1L)).thenReturn(new DriverResponseDto());
        when(passengerServiceClient.getPassengerById(1L)).thenReturn(new PassengerResponseDto());
        when(paymentServiceClient.getPaymentByRideId(1L)).thenReturn(new PaymentResponseDto());

        RideResponseDto result = rideService.getRideById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void getAllRides_ShouldReturnAllRides() {
        Route route = new Route();
        route.setId(1L);
        route.setStartAddress("Start Address");
        route.setEndAddress("End Address");
        route.setDistance(10.0f);
        route.setEstimatedTime(30);
        routeRepository.save(route);

        Ride ride = new Ride();
        ride.setId(1L);
        ride.setDriverId(1L);
        ride.setPassengerId(1L);
        ride.setRoute(route);
        ride.setStatus("PENDING");
        ride.setAmount(BigDecimal.valueOf(100.00));
        rideRepository.save(ride);

        when(driverServiceClient.getDriverById(1L)).thenReturn(new DriverResponseDto());
        when(passengerServiceClient.getPassengerById(1L)).thenReturn(new PassengerResponseDto());
        when(paymentServiceClient.getPaymentByRideId(1L)).thenReturn(new PaymentResponseDto());

        RideListResponseDto result = rideService.getAllRides();

        assertNotNull(result);
        assertEquals(1, result.getRide().size());
        assertEquals(1L, result.getRide().get(0).getId());
        assertEquals("PENDING", result.getRide().get(0).getStatus());
    }

    @Test
    void updateRide_ShouldUpdateRide_WhenValidRequest() {
        // Arrange
        Route route = new Route();
        route.setId(1L);
        route.setStartAddress("Start Address");
        route.setEndAddress("End Address");
        route.setDistance(10.0f);
        route.setEstimatedTime(30);
        routeRepository.save(route);

        Ride ride = new Ride();
        ride.setId(1L);
        ride.setDriverId(1L);
        ride.setPassengerId(1L);
        ride.setRoute(route);
        ride.setStatus("PENDING");
        ride.setAmount(BigDecimal.valueOf(100.00));
        rideRepository.save(ride);

        RideRequestDto rideRequestDto = new RideRequestDto();
        rideRequestDto.setDriverId(1L);
        rideRequestDto.setPassengerId(1L);
        rideRequestDto.setRouteId(1L);
        rideRequestDto.setStatus("ACCEPTED");

        when(driverServiceClient.getDriverById(1L)).thenReturn(new DriverResponseDto());
        when(passengerServiceClient.getPassengerById(1L)).thenReturn(new PassengerResponseDto());

        RideResponseDto result = rideService.updateRide(1L, rideRequestDto);

        assertNotNull(result);
        assertEquals("ACCEPTED", result.getStatus());
        assertEquals(1L, result.getId());
    }


}