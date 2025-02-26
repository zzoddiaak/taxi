package rating_service.rating_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rating_service.rating_service.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}