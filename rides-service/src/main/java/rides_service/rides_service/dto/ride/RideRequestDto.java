package rides_service.rides_service.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {
    private Long driverId;
    private Long passengerId;
    private Long routeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String promoCode;
    private String paymentMethod;

}