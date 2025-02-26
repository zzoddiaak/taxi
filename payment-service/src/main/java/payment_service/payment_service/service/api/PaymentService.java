package payment_service.payment_service.service.api;

import payment_service.payment_service.dto.PaymentListResponseDto;
import payment_service.payment_service.dto.PaymentRequestDto;
import payment_service.payment_service.dto.PaymentResponseDto;

import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto);
    PaymentResponseDto getPaymentById(Long id);
    PaymentListResponseDto getAllPayments();
    PaymentResponseDto updatePayment(Long id, PaymentRequestDto paymentRequestDto);
    void deletePayment(Long id);
}