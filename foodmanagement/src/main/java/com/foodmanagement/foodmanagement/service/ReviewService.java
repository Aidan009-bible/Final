package com.foodmanagement.foodmanagement.service;


import com.foodmanagement.foodmanagement.dto.ReviewDTO;
import com.foodmanagement.foodmanagement.entity.Review;
import com.foodmanagement.foodmanagement.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Double getAverageRating() {
        return reviewRepository.findAverageRating();
    }

    public List<ReviewDTO> getRecentReviews() {
        return reviewRepository.findTop3ByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Long getTotalRatingCount() {
        return reviewRepository.count();
    }

    public Long getStarRatingCount(int stars) {
        return reviewRepository.countByRating(stars);
    }

    public Review createReview(ReviewDTO reviewDTO) {
        Review review = new Review();
        review.setName(reviewDTO.getName());
        review.setEmail(reviewDTO.getEmail());
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setCreatedAt(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }
    

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setName(review.getName());
        dto.setEmail(review.getEmail());  // Add this line
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
    
}