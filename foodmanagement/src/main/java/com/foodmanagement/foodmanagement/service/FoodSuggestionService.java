package com.foodmanagement.foodmanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodmanagement.foodmanagement.entity.Food;
import com.foodmanagement.foodmanagement.entity.FoodSuggestion;
import com.foodmanagement.foodmanagement.repository.FoodRepository;
import com.foodmanagement.foodmanagement.repository.FoodSuggestionRepository;

@Service
public class FoodSuggestionService {
    
    @Autowired
    private FoodSuggestionRepository suggestionRepository;
    
    @Autowired
    private FoodRepository foodRepository;
    
    public List<Food> getSuggestedFoods(Integer foodId) {
        return suggestionRepository.findByFoodIdOrderByPurchaseCountDesc(foodId)
            .stream()
            .map(FoodSuggestion::getSuggestedFood)
            .limit(3)
            .collect(Collectors.toList());
    }
    
    public void recordPurchaseTogether(Integer foodId, Integer suggestedFoodId) {
        FoodSuggestion suggestion = suggestionRepository
            .findByFoodIdAndSuggestedFoodId(foodId, suggestedFoodId)
            .orElse(new FoodSuggestion());
            
        if (suggestion.getId() == null) {
            suggestion.setFood(foodRepository.findById(foodId).orElse(null));
            suggestion.setSuggestedFood(foodRepository.findById(suggestedFoodId).orElse(null));
            suggestion.setPurchaseCount(0);
        }
        
        suggestion.setPurchaseCount(suggestion.getPurchaseCount() + 1);
        suggestion.setLastPurchased(LocalDateTime.now());
        suggestionRepository.save(suggestion);
    }
} 