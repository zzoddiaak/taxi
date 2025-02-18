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
    private Integer phoneNumber;

    @Column(name = "license_number", nullable = false)
    private Integer licenseNumber;

    @Column(name = "license_number")
    private String carModel;

    @Column(name = "car_plate_number")
    private String carPlateNumber;

}
