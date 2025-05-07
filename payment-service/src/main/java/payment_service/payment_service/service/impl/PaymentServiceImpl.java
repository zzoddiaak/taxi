package payment_service.payment_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import payment_service.payment_service.config.mapper.DtoMapper;
import payment_service.payment_service.dto.passenger.BalanceUpdateDto;
import payment_service.payment_service.dto.payment.PaymentListResponseDto;
import payment_service.payment_service.dto.payment.PaymentRequestDto;
import payment_service.payment_service.dto.payment.PaymentResponseDto;
import payment_service.payment_service.entity.Payment;
import payment_service.payment_service.entity.PromoCode;
import payment_service.payment_service.exception.payment.PaymentNotFoundException;
import payment_service.payment_service.exception.payment.PaymentProcessingException;
import payment_service.payment_service.repository.PaymentRepository;
import payment_service.payment_service.service.api.PassengerServiceClient;
import payment_service.payment_service.service.api.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PromoCodeService promoCodeService;
    private final PassengerServiceClient passengerServiceClient;
    private final DtoMapper mapper;

    @Override
    public void updatePaymentStatus(Long id, String status) {
        log.info("Updating payment status: paymentId={}, newStatus={}", id, status);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Payment not found for update: paymentId={}", id);
                    return new PaymentNotFoundException("Payment not found with id: " + id);
                });

        if (!"cash".equalsIgnoreCase(payment.getPaymentMethod())) {
            log.error("Invalid payment method for status update: {}", payment.getPaymentMethod());
            throw new IllegalArgumentException("Payment status can only be updated for cash payments");
        }

        payment.setStatus(status);
        paymentRepository.save(payment);
        log.debug("Payment status updated successfully");
    }

    @Override
    public PaymentResponseDto getPaymentByRideId(Long rideId) {
        log.debug("Fetching payment by rideId: {}", rideId);
        Payment payment = paymentRepository.findByRideId(rideId)
                .orElseThrow(() -> {
                    log.error("Payment not found for rideId: {}", rideId);
                    return new PaymentNotFoundException("Payment not found for ride id: " + rideId);
                });
        return mapper.convertToDto(payment, PaymentResponseDto.class);
    }

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        log.info("Creating new payment for ride: {}", paymentRequestDto.getRideId());
        PromoCode promoCode = null;
        BigDecimal finalAmount = paymentRequestDto.getAmount();

        if (paymentRequestDto.getPromoCode() != null) {
            log.debug("Applying promo code: {}", paymentRequestDto.getPromoCode());
            promoCode = promoCodeService.getPromoCodeByCode(paymentRequestDto.getPromoCode());
            finalAmount = promoCodeService.applyDiscount(paymentRequestDto.getAmount(), paymentRequestDto.getPromoCode());
        }

        Payment payment = Payment.builder()
                .rideId(paymentRequestDto.getRideId())
                .passengerId(paymentRequestDto.getPassengerId())
                .amount(finalAmount)
                .paymentMethod(paymentRequestDto.getPaymentMethod())
                .status("pending")
                .createdAt(LocalDateTime.now())
                .promoCode(promoCode)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully: paymentId={}", savedPayment.getId());
        return mapper.convertToDto(savedPayment, PaymentResponseDto.class);
    }

    @Override
    public PaymentResponseDto getPaymentById(Long id) {
        log.debug("Fetching payment by ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Payment not found: paymentId={}", id);
                    return new PaymentNotFoundException("Payment not found");
                });
        return mapper.convertToDto(payment, PaymentResponseDto.class);
    }

    @Override
    public PaymentListResponseDto getAllPayments() {
        log.info("Fetching all payments");
        List<Payment> payments = paymentRepository.findAll();
        log.debug("Found {} payments", payments.size());

        List<PaymentResponseDto> paymentResponseDtos = payments.stream()
                .map(payment -> mapper.convertToDto(payment, PaymentResponseDto.class))
                .toList();

        return new PaymentListResponseDto(paymentResponseDtos);
    }

    @Override
    public PaymentResponseDto updatePayment(Long id, PaymentRequestDto paymentRequestDto) {
        log.info("Updating payment: paymentId={}", id);
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Payment not found for update: paymentId={}", id);
                    return new PaymentNotFoundException("Payment not found");
                });

        existingPayment.setRideId(paymentRequestDto.getRideId());
        existingPayment.setAmount(paymentRequestDto.getAmount());
        existingPayment.setPaymentMethod(paymentRequestDto.getPaymentMethod());
        existingPayment.setStatus(paymentRequestDto.getStatus());

        if (paymentRequestDto.getPromoCode() != null) {
            log.debug("Updating promo code: {}", paymentRequestDto.getPromoCode());
            PromoCode promoCode = promoCodeService.getPromoCodeByCode(paymentRequestDto.getPromoCode());
            existingPayment.setPromoCode(promoCode);
            BigDecimal discountedAmount = promoCodeService.applyDiscount(paymentRequestDto.getAmount(), paymentRequestDto.getPromoCode());
            existingPayment.setAmount(discountedAmount);
        }

        Payment updatedPayment = paymentRepository.save(existingPayment);
        log.info("Payment updated successfully: paymentId={}", id);
        return mapper.convertToDto(updatedPayment, PaymentResponseDto.class);
    }

    @Override
    public void deletePayment(Long id) {
        log.info("Deleting payment: paymentId={}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Payment not found for deletion: paymentId={}", id);
                    return new PaymentNotFoundException("Payment not found");
                });
        paymentRepository.delete(payment);
        log.debug("Payment deleted successfully");
    }
}