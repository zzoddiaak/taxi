package passenger_service.passenger_service.component;

import passenger_service.passenger_service.dto.passenger.PassengerRequestDto;
import passenger_service.passenger_service.dto.passenger.PassengerResponseDto;
import passenger_service.passenger_service.exception.passenger.InsufficientBalanceException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TestContext {
    private PassengerRequestDto passengerRequest;
    private PassengerResponseDto passengerResponse;
    private Long passengerId;
    private Double currentRating;
    private Integer ratingCount;
    private Double currentBalance;
    private InsufficientBalanceException exception;
}