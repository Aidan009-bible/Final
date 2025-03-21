package com.foodmanagement.foodmanagement.Controller;


import com.foodmanagement.foodmanagement.dto.ReviewDTO;
import com.foodmanagement.foodmanagement.entity.Review;
import com.foodmanagement.foodmanagement.service.ReviewService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Get average rating for restaurant
    @GetMapping("/restaurant/average-rating")
    public ResponseEntity<Double> getAverageRating() {
        try {
            Double avgRating = reviewService.getAverageRating();
            // Round to 1 decimal place
            double roundedRating = Math.round(avgRating * 10.0) / 10.0;
            return ResponseEntity.ok(roundedRating);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get recent reviews
    @GetMapping("/restaurant/recent")
    public ResponseEntity<List<ReviewDTO>> getRecentReviews() {
        try {
            List<ReviewDTO> recentReviews = reviewService.getRecentReviews();
            return ResponseEntity.ok(recentReviews);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get total review count
    @GetMapping("/restaurant/total-rating-count")
    public ResponseEntity<Long> getTotalRatingCount() {
        try {
            Long totalCount = reviewService.getTotalRatingCount();
            return ResponseEntity.ok(totalCount);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get count of reviews by star rating
    @GetMapping("/restaurant/{stars}-stars-count")
    public ResponseEntity<Long> getStarRatingCount(@PathVariable int stars) {
        try {
            if (stars < 1 || stars > 5) {
                return ResponseEntity.badRequest().build();
            }
            Long count = reviewService.getStarRatingCount(stars);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Post a new review
    @PostMapping("/restaurant")
    public ResponseEntity<Review> createReview(@RequestBody ReviewDTO reviewDTO) {
        Review review = reviewService.createReview(reviewDTO);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
}
}