package com.foodmanagement.foodmanagement.entity;

//import java.time.LocalDateTime;

import jakarta.persistence.*;

//import java.math.BigDecimal; // Import BigDecimal
@Entity
@Table(name = "orderline")
@IdClass(OrderlineId.class) // Composite key handler
public class Orderline {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Ordertable order;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(nullable = false)
    private Long quantity;

    @Column
    private Integer rating = 0; // Default value 0

    @Column(columnDefinition = "TEXT")
    private String review;

    @Column(name = "beverage_size_id")
    private Integer beverageSizeId;

    @Column(nullable = false)
    private Double price = 0.0; // Changed from double to Double with default value

    @Column(name = "food_title")
    private String foodTitle;

    @Column(name = "total_amount")
    private Double totalAmount;

    // Default constructor
    public Orderline() {
        this.price = 0.0; // Set default value in default constructor
    }

    // Constructor with required fields
    public Orderline(Ordertable order, Food food, Long quantity, Double price) {
        this.order = order;
        this.food = food;
        this.quantity = quantity;
        this.price = price != null ? price : 0.0;
        this.foodTitle = food.getTitle();
    }

    // Getters and Setters
    public Ordertable getOrder() {
        return order;
    }

    public void setOrder(Ordertable order) {
        this.order = order;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
        this.foodTitle = food.getTitle(); // Update food title when food is set
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getBeverageSizeId() {
        return beverageSizeId;
    }

    public void setBeverageSizeId(Integer beverageSizeId) {
        this.beverageSizeId = beverageSizeId;
    }

    public Double getPrice() {
        return price != null ? price : 0.0;
    }

    public void setPrice(Double price) {
        this.price = price != null ? price : 0.0;
    }

    public String getFoodTitle() {
        return foodTitle;
    }

    public void setFoodTitle(String foodTitle) {
        this.foodTitle = foodTitle;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Orderline{" +
                "order=" + (order != null ? order.getId() : null) +
                ", food=" + (food != null ? food.getId() : null) +
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
