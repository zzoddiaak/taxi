package rides_service.rides_service.unit.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import rides_service.rides_service.service.kafka.KafkaProducerService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    void sendAvailableRide_ShouldSendMessageToKafka() {
        String rideId = "1";

        kafkaProducerService.sendAvailableRide(rideId);

        verify(kafkaTemplate, times(1)).send("available-rides", rideId);
    }

    @Test
    void sendRideAcceptance_ShouldSendMessageToKafka() {
        String rideId = "1";
        String driverId = "1";
        boolean accepted = true;

        kafkaProducerService.sendRideAcceptance(rideId, driverId, accepted);

        verify(kafkaTemplate, times(1)).send("ride-acceptance", rideId + ":" + driverId + ":" + accepted);
    }

    @Test
    void sendRideStart_ShouldSendMessageToKafka() {
        String rideId = "1";

        kafkaProducerService.sendRideStart(rideId);

        verify(kafkaTemplate, times(1)).send("ride-start", rideId);
    }

    @Test
    void sendRideEnd_ShouldSendMessageToKafka() {
        String rideId = "1";

        kafkaProducerService.sendRideEnd(rideId);

        verify(kafkaTemplate, times(1)).send("ride-end", rideId);
    }

    @Test
    void sendRideCompleted_ShouldSendMessageToKafka() {
        String rideId = "1";
        String passengerId = "1";
        String amount = "100.00";

        kafkaProducerService.sendRideCompleted(rideId, passengerId, amount);

        verify(kafkaTemplate, times(1)).send("ride-completed", rideId + ":" + passengerId + ":" + amount);
    }
}