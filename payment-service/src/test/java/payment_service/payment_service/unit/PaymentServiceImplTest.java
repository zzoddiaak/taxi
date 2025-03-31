package payment_service.payment_service.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import payment_service.payment_service.config.mapper.DtoMapper;
import payment_service.payment_service.dto.payment.PaymentListResponseDto;
import payment_service.payment_service.dto.payment.PaymentResponseDto;
import payment_service.payment_service.entity.Payment;
import payment_service.payment_service.repository.PaymentRepository;
import payment_service.payment_service.service.api.PassengerServiceClient;
import payment_service.payment_service.service.impl.PaymentServiceImpl;
import payment_service.payment_service.service.impl.PromoCodeService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PromoCodeService promoCodeService;

    @Mock
    private PassengerServiceClient passengerServiceClient;

    @Mock
    private DtoMapper mapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void updatePaymentStatus_ShouldUpdateStatus_WhenPaymentExistsAndIsCash() {
        Long paymentId = 1L;
        String newStatus = "COMPLETED";
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setPaymentMethod("cash");
        payment.setStatus("PENDING");

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        paymentService.updatePaymentStatus(paymentId, newStatus);

        assertEquals(newStatus, payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void updatePaymentStatus_ShouldThrowException_WhenPaymentIsNotCash() {
        Long paymentId = 1L;
        String newStatus = "COMPLETED";
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setPaymentMethod("CARD");
        payment.setStatus("PENDING");

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThrows(IllegalArgumentException.class, () -> paymentService.updatePaymentStatus(paymentId, newStatus));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void getPaymentByRideId_ShouldReturnPaymentResponseDto_WhenPaymentExists() {
        Long rideId = 1L;
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setRideId(rideId);

        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setId(1L);
        paymentResponseDto.setRideId(rideId);

        when(paymentRepository.findByRideId(rideId)).thenReturn(Optional.of(payment));
        when(mapper.convertToDto(payment, PaymentResponseDto.class)).thenReturn(paymentResponseDto);

        PaymentResponseDto result = paymentService.getPaymentByRideId(rideId);

        assertNotNull(result);
        assertEquals(rideId, result.getRideId());
        verify(paymentRepository, times(1)).findByRideId(rideId);
    }

    @Test
    void getAllPayments_ShouldReturnListOfPayments() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setRideId(1L);

        List<Payment> payments = Collections.singletonList(payment);
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setId(1L);
        paymentResponseDto.setRideId(1L);

        when(paymentRepository.findAll()).thenReturn(payments);
        when(mapper.convertToDto(payment, PaymentResponseDto.class)).thenReturn(paymentResponseDto);

        PaymentListResponseDto result = paymentService.getAllPayments();

        assertNotNull(result);
        assertEquals(1, result.getPayments().size());
        verify(paymentRepository, times(1)).findAll();
    }
}