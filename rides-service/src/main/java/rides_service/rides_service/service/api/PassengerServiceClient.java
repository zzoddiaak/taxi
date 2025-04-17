package rides_service.rides_service.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import rides_service.rides_service.dto.passenger.PassengerResponseDto;

@FeignClient(name = "passenger-service")
public interface PassengerServiceClient {
    @GetMapping("/api/passengers/{id}")
    PassengerResponseDto getPassengerById(@PathVariable Long id);
}
