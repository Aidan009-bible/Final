package com.foodmanagement.foodmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderfactsDTO {
    private Integer totalOrders;
    private Long totalQuantity;
    private Double totalSales;
    private Integer totalUsers;
} 