package com.foodmanagement.foodmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderlineDTO {
    private Integer orderId;
    private Integer foodId;
    private Long quantity;
    private Integer rating = 0; // Default value 0
    private String review;
    private Integer beverageSizeId;
    private double price;
    private String foodTitle;
    private Double totalAmount; // Changed from subtotal

    // Constructor with required fields
    public OrderlineDTO(Integer orderId, Integer foodId, Long quantity, double price, String foodTitle,
            Double totalAmount) {
        this.orderId = orderId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.price = price;
        this.foodTitle = foodTitle;
        this.totalAmount = totalAmount;
        this.rating = 0; // Set default rating
    }

    @Override
    public String toString() {
        return "OrderlineDTO{" +
                "orderId=" + orderId +
                ", foodId=" + foodId +
                ", quantity=" + quantity +
                ", rating=" + rating +
                ", review='" + review + '\'' +
                ", beverageSizeId=" + beverageSizeId +
                ", price=" + price +
                ", foodTitle='" + foodTitle + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
