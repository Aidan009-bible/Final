package com.foodmanagement.foodmanagement.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class FoodSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;
    
    @ManyToOne
    @JoinColumn(name = "suggested_food_id")
    private Food suggestedFood;
    
    private Integer purchaseCount = 0;
    
    @Column(name = "last_purchased")
    private LocalDateTime lastPurchased;
} 