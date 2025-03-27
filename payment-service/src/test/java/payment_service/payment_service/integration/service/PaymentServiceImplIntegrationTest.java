package payment_service.payment_service.integration.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import payment_service.payment_service.dto.payment.PaymentRequestDto;
import payment_service.payment_service.dto.payment.PaymentResponseDto;
import payment_service.payment_service.entity.Payment;
import payment_service.payment_service.repository.PaymentRepository;
import payment_service.payment_service.service.impl.PaymentServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(topics = {"ride-completed"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PaymentServiceImplIntegrationTest {

    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
    }

    @Test
    void createPayment_ShouldCreatePayment() {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setRideId(1L);
        paymentRequestDto.setPassengerId(1L);
        paymentRequestDto.setAmount(BigDecimal.valueOf(100));
        paymentRequestDto.setPaymentMethod("CARD");
        paymentRequestDto.setStatus("pending");

        PaymentResponseDto result = paymentService.createPayment(paymentRequestDto);

        assertNotNull(result);
        assertEquals(1L, result.getRideId());
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        assertEquals("CARD", result.getPaymentMethod());
        assertEquals("pending", result.getStatus());

        List<Payment> payments = paymentRepository.findAll();
        assertEquals(1, payments.size());
        assertEquals(1L, payments.get(0).getRideId());
    }

    @Test
    void getPaymentById_ShouldReturnPayment() {
        Payment payment = new Payment();
        payment.setRideId(1L);
        payment.setPassengerId(1L);
        payment.setAmount(BigDecimal.valueOf(100.11));
        payment.setPaymentMethod("CARD");
        payment.setStatus("pending");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        PaymentResponseDto result = paymentService.getPaymentById(payment.getId());

        assertNotNull(result);
        assertEquals(1L, result.getRideId());
        assertEquals(BigDecimal.valueOf(100.11), result.getAmount());
        assertEquals("CARD", result.getPaymentMethod());
        assertEquals("pending", result.getStatus());
    }

    @Test
    void updatePaymentStatus_ShouldUpdateStatus() {
        Payment payment = new Payment();
        payment.setRideId(1L);
        payment.setPassengerId(1L);
        payment.setAmount(BigDecimal.valueOf(100));
        payment.setPaymentMethod("CASH");
        payment.setStatus("pending");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        paymentService.updatePaymentStatus(payment.getId(), "COMPLETED");

        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        assertEquals("COMPLETED", updatedPayment.getStatus());
    }

    @Test
    void deletePayment_ShouldDeletePayment() {
        Payment payment = new Payment();
        payment.setRideId(1L);
        payment.setPassengerId(1L);
        payment.setAmount(BigDecimal.valueOf(100));
        payment.setPaymentMethod("CARD");
        payment.setStatus("pending");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        paymentService.deletePayment(payment.getId());

        assertFalse(paymentRepository.existsById(payment.getId()));
    }
}