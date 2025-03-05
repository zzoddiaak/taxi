package passenger_service.passenger_service.entity;

import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "passengers")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Double averageRating;
    private Integer ratingCount;
    private Double driverRating;

    @OneToOne(mappedBy = "passenger", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FinancialData financialData;
}



