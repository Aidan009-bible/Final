package com.foodmanagement.foodmanagement.entity;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Integer stock;
    
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double rating = 0.0;
    
    @Column(name = "is_available")
    private Boolean isAvailable;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Lob
    private byte[] image;
    
    private Double discount;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    
    @Column(name = "total_sale_quantity")
    private Integer totalSaleQuantity;
    
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer popularity = 0;
    
    @ManyToMany
    @JoinTable(
        name = "FoodTopping",
        joinColumns = @JoinColumn(name = "food_id"),
        inverseJoinColumns = @JoinColumn(name = "topping_id")
    )
    private Set<Topping> toppings;
}