package com.foodmanagement.foodmanagement.dto;

import java.time.LocalDateTime;
import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;
import com.foodmanagement.foodmanagement.entity.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdersummaryDTO {
    private Integer orderId;
    private String orderCode;
    private LocalDateTime orderDate;
    private double totalAmount;
    private PaymentMethod paymentMethod;
    private Long quantity;
    private String foodTitle;
    private double price;
    private String name;
    private String phoneNumber;
    private String address;
    private OrderStatus status;

    // Constructor with required fields
    public OrdersummaryDTO(Integer orderId, String orderCode, LocalDateTime orderDate, String name,
            PaymentMethod paymentMethod, String foodTitle, double price,
            double totalAmount, Long quantity, OrderStatus status) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.orderDate = orderDate;
        this.name = name;
        this.paymentMethod = paymentMethod;
        this.foodTitle = foodTitle;
        this.price = price;
        this.totalAmount = totalAmount;
        this.quantity = quantity;
        this.status = status;
    }
}