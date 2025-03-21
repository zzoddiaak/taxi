package rating_service.rating_service.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rating_service.rating_service.dto.RatingUpdateDto;

@FeignClient(name = "driver-service", url = "http://driver-service:8085/api/v1/drivers")
public interface DriverServiceClient {

    @PutMapping("/{id}/rating")
    ResponseEntity<Void> updateDriverRating(@PathVariable Long id, @RequestBody RatingUpdateDto ratingUpdateDto);
}
