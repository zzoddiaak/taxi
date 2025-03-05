package passenger_service.passenger_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import passenger_service.passenger_service.entity.FinancialData;

public interface FinancialDataRepository extends JpaRepository<FinancialData, Long> {

}
