package com.foodmanagement.foodmanagement.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryDTO {
    private Integer id;
    private String name;
    private String description;
    private String image;      // Base64-encoded image
    private Integer foodCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}