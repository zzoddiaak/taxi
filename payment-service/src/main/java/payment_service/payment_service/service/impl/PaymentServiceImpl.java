package payment_service.payment_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import payment_service.payment_service.config.mapper.DtoMapper;
import payment_service.payment_service.dto.payment.PaymentListResponseDto;
import payment_service.payment_service.dto.payment.PaymentRequestDto;
import payment_service.payment_service.dto.payment.PaymentResponseDto;
import payment_service.payment_service.entity.Payment;
import payment_service.payment_service.entity.PromoCode;
import payment_service.payment_service.exception.payment.PaymentNotFoundException;
import payment_service.payment_service.repository.PaymentRepository;
import payment_service.payment_service.service.api.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PromoCodeService promoCodeService;
    private final DtoMapper mapper;

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        PromoCode promoCode = null;
        if (paymentRequestDto.getPromoCode() != null) {
            promoCode = promoCodeService.getPromoCodeByCode(paymentRequestDto.getPromoCode());
        }

        Payment payment = Payment.builder()
                .rideId(paymentRequestDto.getRideId())
                .amount(paymentRequestDto.getAmount())
                .paymentMethod(paymentRequestDto.getPaymentMethod())
                .status(paymentRequestDto.getStatus())
                .createdAt(LocalDateTime.now())
                .promoCode(promoCode)
                .build();

        if (promoCode != null) {
            BigDecimal discountedAmount = promoCodeService.applyDiscount(paymentRequestDto.getAmount(), paymentRequestDto.getPromoCode());
            payment.setAmount(discountedAmount);
        }

        Payment savedPayment = paymentRepository.save(payment);

        return mapper.convertToDto(savedPayment, PaymentResponseDto.class);
    }

    @Override
    public PaymentResponseDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        return mapper.convertToDto(payment, PaymentResponseDto.class);
    }

    @Override
    public PaymentListResponseDto getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        List<PaymentResponseDto> paymentResponseDtos = payments.stream()
                .map(payment -> mapper.convertToDto(payment, PaymentResponseDto.class))
                .toList();

        return new PaymentListResponseDto(paymentResponseDtos);
    }

    @Override
    public PaymentResponseDto updatePayment(Long id, PaymentRequestDto paymentRequestDto) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        existingPayment.setRideId(paymentRequestDto.getRideId());
        existingPayment.setAmount(paymentRequestDto.getAmount());
        existingPayment.setPaymentMethod(paymentRequestDto.getPaymentMethod());
        existingPayment.setStatus(paymentRequestDto.getStatus());

        if (paymentRequestDto.getPromoCode() != null) {
            PromoCode promoCode = promoCodeService.getPromoCodeByCode(paymentRequestDto.getPromoCode());
            existingPayment.setPromoCode(promoCode);
            BigDecimal discountedAmount = promoCodeService.applyDiscount(paymentRequestDto.getAmount(), paymentRequestDto.getPromoCode());
            existingPayment.setAmount(discountedAmount);
        }

        Payment updatedPayment = paymentRepository.save(existingPayment);
        return mapper.convertToDto(updatedPayment, PaymentResponseDto.class);
    }

    @Override
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        paymentRepository.delete(payment);
    }
}

