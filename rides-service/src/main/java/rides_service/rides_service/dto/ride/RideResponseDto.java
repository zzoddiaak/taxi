package rides_service.rides_service.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rides_service.rides_service.dto.route.RouteResponseDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideResponseDto {

    private Long id;
    private Long driverId;
    private Long passengerId;
    private RouteResponseDto route;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}