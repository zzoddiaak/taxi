package rides_service.rides_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "routes")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_address", nullable = false)
    private String startAddress;

    @Column(name = "end_address", nullable = false)
    private String endAddress;

    @Column(name = "distance", nullable = false)
    private Float distance;

    @Column(name = "estimated_time", nullable = false)
    private Integer estimatedTime;

    @OneToMany(mappedBy = "route")
    private List<Ride> rides;
}
