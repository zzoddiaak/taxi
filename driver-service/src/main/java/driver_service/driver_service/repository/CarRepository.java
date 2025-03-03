package driver_service.driver_service.repository;

import driver_service.driver_service.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
