package com.foodmanagement.foodmanagement.Controller;

import com.foodmanagement.foodmanagement.dto.ReviewDTO;
import com.foodmanagement.foodmanagement.service.AdminReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurant-reviews")
public class AdminReviewController {

    @Autowired
    private AdminReviewService adminReviewService;

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(adminReviewService.getAllReviews());
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer reviewId) {
        adminReviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}