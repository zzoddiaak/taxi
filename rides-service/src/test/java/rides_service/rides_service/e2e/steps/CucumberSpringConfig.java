package rides_service.rides_service.e2e.steps;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import rides_service.rides_service.RidesServiceApplication;
import rides_service.rides_service.e2e.context.TestContext;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@CucumberContextConfiguration
@SpringBootTest(classes = {RidesServiceApplication.class, CucumberSpringConfig.TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@EmbeddedKafka(
        topics = {
                "available-rides",
                "ride-start",
                "ride-acceptance",
                "ride-end",
                "ride-completed",
                "passenger-rating-topic",
                "driver-rating-topic"
        },
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092",
                "log.dir=target/kafka-logs"
        },
        controlledShutdown = true
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
public class CucumberSpringConfig {

    @Configuration
    public static class TestConfig {
        @Bean
        public TestContext testContext() {
            return new TestContext();
        }

        @Bean(name = "passengerWireMock")
        public WireMockServer passengerWireMock() {
            WireMockServer server = new WireMockServer(wireMockConfig().dynamicPort());
            log.info("Creating Passenger WireMock server");
            return server;
        }

        @Bean(name = "driverWireMock")
        public WireMockServer driverWireMock() {
            WireMockServer server = new WireMockServer(wireMockConfig().dynamicPort());
            log.info("Creating Driver WireMock server");
            return server;
        }

        @Bean(name = "paymentWireMock")
        public WireMockServer paymentWireMock() {
            WireMockServer server = new WireMockServer(wireMockConfig().dynamicPort());
            log.info("Creating Payment WireMock server");
            return server;
        }

        @Bean(name = "rideWireMock")
        public WireMockServer rideWireMock() {
            WireMockServer server = new WireMockServer(wireMockConfig().dynamicPort());
            log.info("Creating Ride WireMock server");
            return server;
        }

        @Bean(name = "ratingWireMock")
        public WireMockServer ratingWireMock() {
            WireMockServer server = new WireMockServer(wireMockConfig().dynamicPort());
            log.info("Creating Rating WireMock server");
            return server;
        }

        @Bean
        public NewTopic availableRidesTopic() {
            return TopicBuilder.name("available-rides").partitions(1).replicas(1).build();
        }

        @Bean
        public NewTopic rideStartTopic() {
            return TopicBuilder.name("ride-start").partitions(1).replicas(1).build();
        }

        @Bean
        public NewTopic rideAcceptanceTopic() {
            return TopicBuilder.name("ride-acceptance").partitions(1).replicas(1).build();
        }

        @Bean
        public NewTopic rideEndTopic() {
            return TopicBuilder.name("ride-end").partitions(1).replicas(1).build();
        }
    }

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

    @PostConstruct
    public void init() {
        passengerWireMock.start();
        driverWireMock.start();
        paymentWireMock.start();
        rideWireMock.start();
        ratingWireMock.start();

        log.info("Passenger WireMock server running on port: {}", passengerWireMock.port());
        log.info("Driver WireMock server running on port: {}", driverWireMock.port());
        log.info("Payment WireMock server running on port: {}", paymentWireMock.port());
        log.info("Ride WireMock server running on port: {}", rideWireMock.port());
        log.info("Rating WireMock server running on port: {}", ratingWireMock.port());
    }
}