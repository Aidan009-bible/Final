package com.foodmanagement.foodmanagement.repository;


import com.foodmanagement.foodmanagement.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {  // Change Long to Integer
    
    @Query("SELECT AVG(r.rating) FROM Review r")
    Double findAverageRating();
    
    List<Review> findTop3ByOrderByCreatedAtDesc();
    
    Long countByRating(int rating);
}