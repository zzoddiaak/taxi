package rides_service.rides_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rides_service.rides_service.entity.Ride;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

}
