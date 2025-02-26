package rides_service.rides_service.dto.ride;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {

    private Long driverId;
    private Long passengerId;
    private String startAddress;
    private String endAddress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Float distance;
    private Integer estimatedTime;
}