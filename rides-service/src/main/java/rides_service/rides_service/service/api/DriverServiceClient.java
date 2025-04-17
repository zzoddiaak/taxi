package rides_service.rides_service.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import rides_service.rides_service.dto.driver.DriverResponseDto;

@FeignClient(name = "driver-service")
public interface DriverServiceClient {
    @GetMapping("/api/v1/drivers/{id}")
    DriverResponseDto getDriverById(@PathVariable Long id);
}