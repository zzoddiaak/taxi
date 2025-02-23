package rides_service.rides_service.dto.route;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDto {

    private String startAddress;
    private String endAddress;
    private Float distance;
    private Integer estimatedTime;
}
