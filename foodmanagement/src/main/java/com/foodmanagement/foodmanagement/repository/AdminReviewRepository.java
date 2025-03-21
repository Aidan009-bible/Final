package com.foodmanagement.foodmanagement.repository;

import com.foodmanagement.foodmanagement.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminReviewRepository extends JpaRepository<Review, Integer> {
    // Changed Long to Integer to match Review entity's ID type
}