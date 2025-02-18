package driver_service.driver_service.repository;

import driver_service.driver_service.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Driver, Long> {

}
