package payment_service.payment_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payment_service.payment_service.dto.payment.PaymentListResponseDto;
import payment_service.payment_service.dto.payment.PaymentRequestDto;
import payment_service.payment_service.dto.payment.PaymentResponseDto;
import payment_service.payment_service.dto.payment.PaymentStatusUpdateDto;
import payment_service.payment_service.service.api.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updatePaymentStatus(@PathVariable Long id, @RequestBody PaymentStatusUpdateDto paymentStatusUpdateDto) {
        paymentService.updatePaymentStatus(id, paymentStatusUpdateDto.getStatus());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<PaymentResponseDto> getPaymentByRideId(@PathVariable Long rideId) {
        PaymentResponseDto paymentResponseDto = paymentService.getPaymentByRideId(rideId);
        return ResponseEntity.ok(paymentResponseDto);
    }
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentRequestDto);
        return new ResponseEntity<>(paymentResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long id) {
        PaymentResponseDto paymentResponseDto = paymentService.getPaymentById(id);
        return ResponseEntity.ok(paymentResponseDto);
    }

    @GetMapping
    public ResponseEntity<PaymentListResponseDto> getAllPayments() {
        PaymentListResponseDto paymentListResponseDto = paymentService.getAllPayments();
        return ResponseEntity.ok(paymentListResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> updatePayment(@PathVariable Long id, @RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto paymentResponseDto = paymentService.updatePayment(id, paymentRequestDto);
        return ResponseEntity.ok(paymentResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
