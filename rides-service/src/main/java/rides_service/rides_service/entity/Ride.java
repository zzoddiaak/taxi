package rides_service.rides_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "rides")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_id", nullable = false)
    private Long driverId; // Идентификатор водителя из другого микросервиса

    @Column(name = "passenger_id", nullable = false)
    private Long passengerId; // Идентификатор пассажира из другого микросервиса

    @Column(name = "start_address", nullable = false)
    private String startAddress;

    @Column(name = "end_address", nullable = false)
    private String endAddress;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "distance", nullable = false)
    private Float distance;

    @Column(name = "estimated_time", nullable = false)
    private Integer estimatedTime;
}