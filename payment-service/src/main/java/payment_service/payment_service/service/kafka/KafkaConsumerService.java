package payment_service.payment_service.service.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import payment_service.payment_service.dto.passenger.BalanceUpdateDto;
import payment_service.payment_service.entity.Payment;
import payment_service.payment_service.exception.payment.PaymentNotFoundException;
import payment_service.payment_service.exception.payment.PaymentProcessingException;
import payment_service.payment_service.repository.PaymentRepository;
import payment_service.payment_service.service.api.PassengerServiceClient;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final PaymentRepository paymentRepository;
    private final PassengerServiceClient passengerServiceClient;

    @KafkaListener(topics = "ride-completed", groupId = "payment-service-group")
    public void listenRideCompleted(ConsumerRecord<String, String> record) {
        processMessage(record);
        String[] parts = record.value().split(":");
        Long rideId = Long.parseLong(parts[0]);
        Long passengerId = Long.parseLong(parts[1]);
        BigDecimal amount = new BigDecimal(parts[2]);

        Payment payment = paymentRepository.findByRideId(rideId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for ride id: " + rideId));

        if ("CARD".equalsIgnoreCase(payment.getPaymentMethod())) {
            try {
                passengerServiceClient.updatePassengerBalance(
                        passengerId,
                        new BalanceUpdateDto(amount.doubleValue())
                );
                payment.setStatus("COMPLETED");
                paymentRepository.save(payment);
            } catch (Exception e) {
                throw new PaymentProcessingException("Failed to update passenger balance: " + e.getMessage());
            }
        }
    }

    private void processMessage(ConsumerRecord<String, String> record) {
        String token = getTokenFromHeaders(record);
        if (token != null) {
            Jwt jwt = Jwt.withTokenValue(token)
                    .header("alg", "RS256")
                    .claim("sub", "service-account")
                    .build();

            SecurityContextHolder.getContext().setAuthentication(
                    new JwtAuthenticationToken(jwt)
            );
        }
    }

    private String getTokenFromHeaders(ConsumerRecord<String, String> record) {
        Iterable<org.apache.kafka.common.header.Header> headers = record.headers().headers("Authorization");
        if (headers.iterator().hasNext()) {
            return new String(headers.iterator().next().value()).replace("Bearer ", "");
        }
        return null;
    }
}