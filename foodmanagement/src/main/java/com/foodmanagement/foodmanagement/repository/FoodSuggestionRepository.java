package com.foodmanagement.foodmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.foodmanagement.foodmanagement.entity.FoodSuggestion;

public interface FoodSuggestionRepository extends JpaRepository<FoodSuggestion, Integer> {
    List<FoodSuggestion> findByFoodIdOrderByPurchaseCountDesc(Integer foodId);
    Optional<FoodSuggestion> findByFoodIdAndSuggestedFoodId(Integer foodId, Integer suggestedFoodId);
} 