package rides_service.rides_service.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rides_service.rides_service.dto.payment.PaymentRequestDto;
import rides_service.rides_service.dto.payment.PaymentResponseDto;

@FeignClient(name = "payment-service", url = "http://localhost:8082/api/payments")
public interface PaymentServiceClient {

    @GetMapping("/ride/{rideId}")
    PaymentResponseDto getPaymentByRideId(@PathVariable Long rideId);

    @PostMapping
    PaymentResponseDto createPayment(@RequestBody PaymentRequestDto paymentRequestDto);

}
