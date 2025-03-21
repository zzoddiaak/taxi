package rides_service.rides_service.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import rides_service.rides_service.dto.driver.DriverResponseDto;

@FeignClient(name = "driver-service", url = "http://driver-service:8085/api/v1/drivers")
public interface DriverServiceClient {

    @GetMapping("/{id}")
    DriverResponseDto getDriverById(@PathVariable Long id);
}
