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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Rating Controller", description = "API for managing ratings")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @Operation(summary = "Create a rating", description = "Creates a new rating")
    public ResponseEntity<RatingResponseDto> createRating(@RequestBody RatingRequestDto ratingRequestDto) {
        RatingResponseDto ratingResponseDto = ratingService.createRating(ratingRequestDto);
        return new ResponseEntity<>(ratingResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rating by ID", description = "Retrieves a rating by its ID")
    public ResponseEntity<RatingResponseDto> getRatingById(@PathVariable Long id) {
        RatingResponseDto ratingResponseDto = ratingService.getRatingById(id);
        return ResponseEntity.ok(ratingResponseDto);
    }

    @GetMapping
    @Operation(summary = "Get all ratings", description = "Retrieves a list of all ratings")
    public ResponseEntity<RatingListResponseDto> getAllRatings() {
        RatingListResponseDto ratings = ratingService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update rating", description = "Updates an existing rating")
    public ResponseEntity<RatingResponseDto> updateRating(@PathVariable Long id, @RequestBody RatingRequestDto ratingRequestDto) {
        RatingResponseDto ratingResponseDto = ratingService.updateRating(id, ratingRequestDto);
        return ResponseEntity.ok(ratingResponseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete rating", description = "Deletes a rating by its ID")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
