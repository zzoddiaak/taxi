package rides_service.rides_service.e2e.steps;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import rides_service.rides_service.e2e.context.TestContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    @Qualifier("passengerWireMock")
    private WireMockServer passengerWireMock;

    @Autowired
    @Qualifier("driverWireMock")
    private WireMockServer driverWireMock;

    @Autowired
    @Qualifier("paymentWireMock")
    private WireMockServer paymentWireMock;

    @Autowired
    @Qualifier("rideWireMock")
    private WireMockServer rideWireMock;

    @Autowired
    @Qualifier("ratingWireMock")
    private WireMockServer ratingWireMock;



    @Before
    public void setup() {
        context.reset();

        startWireMockServers();

        setServiceUrls();

        setupAllServiceMocks();

        configureKafka();
    }

    private void startWireMockServers() {
        startServerWithRetry(passengerWireMock, "Passenger");
        startServerWithRetry(driverWireMock, "Driver");
        startServerWithRetry(paymentWireMock, "Payment");
        startServerWithRetry(rideWireMock, "Ride");
        startServerWithRetry(ratingWireMock, "Rating");
    }

    private void startServerWithRetry(WireMockServer server, String serverName) {
        try {
            if (!server.isRunning()) {
                server.start();
                System.out.println(serverName + " WireMock server started on port: " + server.port());
            }
        } catch (Exception e) {
            System.err.println("Failed to start " + serverName + " WireMock server: " + e.getMessage());
            // Попытка остановить и перезапустить сервер
            try {
                if (server.isRunning()) {
                    server.stop();
                }
                server.start();
                System.out.println(serverName + " WireMock server restarted on port: " + server.port());
            } catch (Exception ex) {
                throw new RuntimeException("Failed to restart " + serverName + " WireMock server", ex);
            }
        }
    }

    private void setServiceUrls() {
        context.setPassengerServiceUrl("http://localhost:" + passengerWireMock.port());
        context.setDriverServiceUrl("http://localhost:" + driverWireMock.port());
        context.setPaymentServiceUrl("http://localhost:" + paymentWireMock.port());
        context.setRideServiceUrl("http://localhost:" + rideWireMock.port());
        context.setRatingServiceUrl("http://localhost:" + ratingWireMock.port());
    }

    private void setupAllServiceMocks() {
        setupPassengerMocks();
        setupDriverMocks();
        setupPaymentMocks();
        setupRideMocks();
        setupRatingMocks();
    }

    private void configureKafka() {
        String brokers = embeddedKafka.getBrokersAsString();
        System.setProperty("spring.kafka.bootstrap-servers", brokers);
        System.out.println("Kafka brokers configured: " + brokers);
    }

    @After
    public void cleanup() {
        context.reset();

        resetAllWireMockServers();

        System.out.println("Test context and WireMock servers reset");
    }

    private void resetAllWireMockServers() {
        passengerWireMock.resetAll();
        driverWireMock.resetAll();
        paymentWireMock.resetAll();
        rideWireMock.resetAll();
        ratingWireMock.resetAll();
    }

    private void setupPassengerMocks() {

        passengerWireMock.stubFor(
                get(urlPathEqualTo("/api/passengers/1"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":1,\"rating\":{\"averageRating\":4.166,\"ratingCount\":6}}")
                                .withStatus(200)));

        passengerWireMock.stubFor(
                get(urlPathMatching("/actuator/health"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"status\":\"UP\"}")
                                .withStatus(200)));

        // Мок для пассажира с рейтингом
        passengerWireMock.stubFor(
                get(urlPathEqualTo("/api/passengers/2"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":2,\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"rating\":{\"averageRating\":4.0,\"ratingCount\":3}}")
                                .withStatus(200)));

        // Мок для обновления рейтинга пассажира
        passengerWireMock.stubFor(
                put(urlPathMatching("/api/passengers/2/rating"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"averageRating\":4.25,\"ratingCount\":4}")
                                .withStatus(200)));


    }

    private void setupDriverMocks() {

        driverWireMock.stubFor(
                post(urlPathMatching("/api/v1/drivers/.*/rate-passenger/.*"))
                        .withQueryParam("rating", matching(".*"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"status\":\"RATED\"}")));

        driverWireMock.stubFor(
                get(urlPathMatching("/actuator/health"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"status\":\"UP\"}")
                                .withStatus(200)));

        // Общий мок для получения информации о водителе
        driverWireMock.stubFor(
                get(urlPathMatching("/api/v1/drivers/.*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":1,\"firstName\":\"Mike\",\"lastName\":\"Johnson\",\"available\":true,\"rating\":{\"averageRating\":4.5,\"ratingCount\":10}}")
                                .withStatus(200)));

        // Мок для обновления рейтинга (используется в сценарии Driver rating updates)
        driverWireMock.stubFor(
                put(urlPathMatching("/api/v1/drivers/1/rating"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"averageRating\":4.363,\"ratingCount\":11}")
                                .withStatus(200)));

        // Мок для другого сценария с рейтингом
        driverWireMock.stubFor(
                put(urlPathMatching("/api/v1/drivers/2/rating"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"averageRating\":4.166,\"ratingCount\":6}")
                                .withStatus(200)));
        // Добавляем мок для завершения поездки
        driverWireMock.stubFor(
                post(urlPathMatching("/api/v1/drivers/.*/end-ride/.*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"status\":\"COMPLETED\"}")
                                .withStatus(200)));
    }

    private void setupPaymentMocks() {
        paymentWireMock.stubFor(
                get(urlPathMatching("/actuator/health"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"status\":\"UP\"}")
                                .withStatus(200)));

        paymentWireMock.stubFor(
                post(urlPathEqualTo("/api/payments"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":\"pay-123\",\"status\":\"PENDING\"}")
                                .withStatus(201)));

        paymentWireMock.stubFor(
                get(urlPathMatching("/api/payments/.*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":\"pay-123\",\"rideId\":\"ride-123\",\"amount\":25.0,\"status\":\"COMPLETED\"}")
                                .withStatus(200)));
    }

    private void setupRideMocks() {
        rideWireMock.stubFor(
                get(urlPathMatching("/actuator/health"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"status\":\"UP\"}")
                                .withStatus(200)));

        rideWireMock.stubFor(
                put(urlPathMatching("/api/rides/.*/status"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"status\":\"ACCEPTED\"}")
                                .withStatus(200)));

        rideWireMock.stubFor(
                post(urlPathEqualTo("/api/rides"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":\"ride-123\",\"status\":\"PENDING\"}")
                                .withStatus(201)));

        rideWireMock.stubFor(
                get(urlPathMatching("/api/rides/.*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":\"ride-123\",\"status\":\"COMPLETED\"}")
                                .withStatus(200)));
    }

    private void setupRatingMocks() {

        ratingWireMock.stubFor(
                get(urlPathMatching("/api/ratings/drivers/.*/average"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"averageRating\":4.5}")
                                .withStatus(200)));

        ratingWireMock.stubFor(
                get(urlPathMatching("/actuator/health"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"status\":\"UP\"}")
                                .withStatus(200)));

        // Мок для получения среднего рейтинга
        ratingWireMock.stubFor(
                get(urlPathMatching("/api/ratings/average/.*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"averageRating\":4.363}")
                                .withStatus(200)));

        // Мок для создания рейтинга
        ratingWireMock.stubFor(
                post(urlPathEqualTo("/api/ratings"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":1,\"driverId\":1,\"passengerId\":1,\"rating\":5.0,\"comment\":\"Great ride\"}")
                                .withStatus(201)));
        driverWireMock.stubFor(
                get(urlPathMatching("/api/v1/drivers/.*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":1,\"firstName\":\"Mike\",\"lastName\":\"Johnson\",\"available\":true,\"rating\":{\"averageRating\":4.5,\"ratingCount\":10}}")
                                .withStatus(200)));
    }


    @Given("all microservices are running")
    public void allMicroservicesAreRunning() {
        assertTrue(passengerWireMock.isRunning(), "Passenger service is not running");
        assertTrue(driverWireMock.isRunning(), "Driver service is not running");
        assertTrue(paymentWireMock.isRunning(), "Payment service is not running");
        assertTrue(rideWireMock.isRunning(), "Ride service is not running");
        assertTrue(ratingWireMock.isRunning(), "Rating service is not running");
    }

    @Given("Kafka message broker is running")
    public void kafkaMessageBrokerIsRunning() {
        String brokers = embeddedKafka.getBrokersAsString();
        assertTrue(brokers != null && !brokers.isEmpty(), "Kafka broker is not running");
    }


}