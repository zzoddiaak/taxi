package driver_service.driver_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Table(name = "drivers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    @Column(name = "car_model")
    private String carModel;

    @Column(name = "car_plate_number")
    private String carPlateNumber;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @Column(name = "passenger_rating")
    private Double passengerRating;
}