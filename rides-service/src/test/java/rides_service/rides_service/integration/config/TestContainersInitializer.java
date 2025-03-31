package rides_service.rides_service.integration.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    private static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"))
                    .withEmbeddedZookeeper();

    static {
        postgresqlContainer.start();
        kafkaContainer.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + postgresqlContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgresqlContainer.getUsername(),
                "spring.datasource.password=" + postgresqlContainer.getPassword(),
                "spring.datasource.driver-class-name=" + postgresqlContainer.getDriverClassName(),
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect",
                "spring.liquibase.enabled=false",

                "spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers(),
                "spring.kafka.consumer.auto-offset-reset=earliest"
        ).applyTo(applicationContext.getEnvironment());
    }
}