package passenger_service.passenger_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "financial_data")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "passenger_id", nullable = false, unique = true)
    private Passenger passenger;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_expiry_date")
    private String cardExpiryDate;

    @Column(name = "card_cvv")
    private String cardCvv;

    @Column(name = "promo")
    private String promo;
}


