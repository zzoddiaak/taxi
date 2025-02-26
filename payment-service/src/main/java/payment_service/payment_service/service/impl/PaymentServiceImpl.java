package payment_service.payment_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import payment_service.payment_service.config.mapper.DtoMapper;
import payment_service.payment_service.dto.PaymentListResponseDto;
import payment_service.payment_service.dto.PaymentRequestDto;
import payment_service.payment_service.dto.PaymentResponseDto;
import payment_service.payment_service.entity.Payment;
import payment_service.payment_service.exception.payment.PaymentNotFoundException;
import payment_service.payment_service.repository.PaymentRepository;
import payment_service.payment_service.service.api.PaymentService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final DtoMapper mapper;

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        Payment payment = mapper.convertToEntity(paymentRequestDto, Payment.class);
        payment.setCreatedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        return mapper.convertToDto(savedPayment, PaymentResponseDto.class);
    }

    @Override
    public PaymentResponseDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment not found with id: " + id)));
        return mapper.convertToDto(payment, PaymentResponseDto.class);
    }

    @Override
    public PaymentListResponseDto getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        List<PaymentResponseDto> paymentResponseDtos = payments.stream()
                .map(payment -> mapper.convertToDto(payment, PaymentResponseDto.class))
                .toList();

        return PaymentListResponseDto.builder()
                .payments(paymentResponseDtos)
                .build();
    }

    @Override
    public PaymentResponseDto updatePayment(Long id, PaymentRequestDto paymentRequestDto) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment not found with id: " + id)));

        existingPayment.setRideId(paymentRequestDto.getRideId());
        existingPayment.setAmount(paymentRequestDto.getAmount());
        existingPayment.setPaymentMethod(paymentRequestDto.getPaymentMethod());
        existingPayment.setStatus(paymentRequestDto.getStatus());

        Payment updatedPayment = paymentRepository.save(existingPayment);
        return mapper.convertToDto(updatedPayment, PaymentResponseDto.class);
    }

    @Override
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment not found with id: " + id)));
        paymentRepository.delete(payment);
    }
}