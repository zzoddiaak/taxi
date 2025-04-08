package payment_service.payment_service.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import payment_service.payment_service.dto.passenger.BalanceUpdateDto;

@FeignClient(name = "passenger-service")
public interface PassengerServiceClient {
    @PutMapping("/api/passengers/{id}/balance")
    ResponseEntity<Void> updatePassengerBalance(
            @PathVariable Long id,
            @RequestBody BalanceUpdateDto balanceUpdateDto
    );
}