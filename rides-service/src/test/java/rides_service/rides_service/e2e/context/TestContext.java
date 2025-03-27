package rides_service.rides_service.e2e.context;

import io.restassured.response.Response;
import lombok.Data;

@Data
public class TestContext {
    private String rideId;
    private String driverId;
    private String passengerId;
    private Response response;
    private Double rideCost;
    private Double driverInitialRating = 0.0;
    private String lastKafkaMessage;
    private Float passengerBalance = 0.0f;

    private String passengerServiceUrl;
    private String driverServiceUrl;
    private String paymentServiceUrl;
    private String rideServiceUrl;
    private String ratingServiceUrl;


    public void reset() {
        passengerBalance = null;
        driverInitialRating = null;
        rideId = null;
        driverId = null;
        passengerId = null;
        response = null;
        rideCost = null;
        passengerServiceUrl = null;
        driverServiceUrl = null;
        paymentServiceUrl = null;
        rideServiceUrl = null;
        ratingServiceUrl = null;
    }
}