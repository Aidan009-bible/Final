package com.foodmanagement.foodmanagement.service;

import com.foodmanagement.foodmanagement.dto.ReviewDTO;
import com.foodmanagement.foodmanagement.entity.Review;
import com.foodmanagement.foodmanagement.repository.AdminReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminReviewService {

    @Autowired
    private AdminReviewRepository reviewRepository;

    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteReview(Integer reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setName(review.getName());
        dto.setEmail((String) review.getEmail());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setDate(review.getCreatedAt());
        return dto;
    }
}