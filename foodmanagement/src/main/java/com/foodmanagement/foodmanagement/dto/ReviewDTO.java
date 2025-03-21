package com.foodmanagement.foodmanagement.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Integer id;
    private String name;
    private String email;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    public void setDate(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}