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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Controller", description = "API for managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PutMapping("/{id}/status")
    @Operation(summary = "Update payment status", description = "Updates the status of a payment")
    public ResponseEntity<Void> updatePaymentStatus(@PathVariable Long id, @RequestBody PaymentStatusUpdateDto paymentStatusUpdateDto) {
        paymentService.updatePaymentStatus(id, paymentStatusUpdateDto.getStatus());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get payment by ride ID", description = "Retrieves a payment by ride ID")
    public ResponseEntity<PaymentResponseDto> getPaymentByRideId(@PathVariable Long rideId) {
        PaymentResponseDto paymentResponseDto = paymentService.getPaymentByRideId(rideId);
        return ResponseEntity.ok(paymentResponseDto);
    }

    @PostMapping
    @Operation(summary = "Create a payment", description = "Creates a new payment")
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentRequestDto);
        return new ResponseEntity<>(paymentResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a payment by its ID")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long id) {
        PaymentResponseDto paymentResponseDto = paymentService.getPaymentById(id);
        return ResponseEntity.ok(paymentResponseDto);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves a list of all payments")
    public ResponseEntity<PaymentListResponseDto> getAllPayments() {
        PaymentListResponseDto paymentListResponseDto = paymentService.getAllPayments();
        return ResponseEntity.ok(paymentListResponseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment", description = "Updates an existing payment")
    public ResponseEntity<PaymentResponseDto> updatePayment(@PathVariable Long id, @RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto paymentResponseDto = paymentService.updatePayment(id, paymentRequestDto);
        return ResponseEntity.ok(paymentResponseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment", description = "Deletes a payment by its ID")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
