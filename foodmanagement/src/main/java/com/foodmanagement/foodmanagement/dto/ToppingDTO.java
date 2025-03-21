package com.foodmanagement.foodmanagement.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ToppingDTO {
    private Integer id;
    private String name;
    private Double price;
    private Boolean isAvailable;
    private Integer foodCount;
    private LocalDateTime modifiedDate;
    private LocalDateTime createdDate;
}