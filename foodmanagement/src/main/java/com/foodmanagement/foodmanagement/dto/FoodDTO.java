package com.foodmanagement.foodmanagement.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

@Data
public class FoodDTO {
    private Integer id;
    private String title;
    private Double price;
    private String description;
    private Integer stock;
    private Double rating;
    private Boolean isAvailable;
    private Integer categoryId;
    private String category;
    private Double discount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime modifiedTime;
    private Integer totalSaleQuantity;
    private Integer popularity;
    private Set<Integer> toppingIds;
    private String image; 
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
