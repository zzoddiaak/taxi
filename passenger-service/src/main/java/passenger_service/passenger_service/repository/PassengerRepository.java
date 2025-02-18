package passenger_service.passenger_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import passenger_service.passenger_service.entity.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

}
