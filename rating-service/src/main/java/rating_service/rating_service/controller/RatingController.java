package rating_service.rating_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rating_service.rating_service.dto.RatingListResponseDto;
import rating_service.rating_service.dto.RatingRequestDto;
import rating_service.rating_service.dto.RatingResponseDto;
import rating_service.rating_service.service.api.RatingService;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class
RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponseDto> createRating(@RequestBody RatingRequestDto ratingRequestDto) {
        RatingResponseDto ratingResponseDto = ratingService.createRating(ratingRequestDto);
        return new ResponseEntity<>(ratingResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingResponseDto> getRatingById(@PathVariable Long id) {
        RatingResponseDto ratingResponseDto = ratingService.getRatingById(id);
        return ResponseEntity.ok(ratingResponseDto);
    }

    @GetMapping
    public ResponseEntity<RatingListResponseDto> getAllRatings() {
        RatingListResponseDto ratings = ratingService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingResponseDto> updateRating(@PathVariable Long id, @RequestBody RatingRequestDto ratingRequestDto) {
        RatingResponseDto ratingResponseDto = ratingService.updateRating(id, ratingRequestDto);
        return ResponseEntity.ok(ratingResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
