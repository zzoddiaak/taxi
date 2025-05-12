package driver_service.driver_service.service.api;


import driver_service.driver_service.dto.payment.PaymentStatusUpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    @PutMapping("/api/payments/{id}/status")
    ResponseEntity<Void> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody PaymentStatusUpdateDto paymentStatusUpdateDto
    );
}