package payment_service.payment_service.service.api;

import payment_service.payment_service.dto.payment.PaymentListResponseDto;
import payment_service.payment_service.dto.payment.PaymentRequestDto;
import payment_service.payment_service.dto.payment.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto);
    PaymentResponseDto getPaymentById(Long id);
    PaymentListResponseDto getAllPayments();
    PaymentResponseDto updatePayment(Long id, PaymentRequestDto paymentRequestDto);
    PaymentResponseDto getPaymentByRideId(Long rideId);
    void deletePayment(Long id);
    void updatePaymentStatus(Long id, String status);
}