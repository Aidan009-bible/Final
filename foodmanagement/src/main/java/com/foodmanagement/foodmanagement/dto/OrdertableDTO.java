package com.foodmanagement.foodmanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;
import com.foodmanagement.foodmanagement.entity.enums.PaymentMethod;

import lombok.Data;

@Data
public class OrdertableDTO {
    private Integer id;
    private Integer userId;
    private String orderCode;
    private LocalDateTime orderDate;

    private OrderStatus status;
    private double totalAmount;

    private PaymentMethod paymentMethod;

    private List<OrderlineDTO> orderLines;

    private String deliAddress;
    private String comment;

    public OrdertableDTO() {
    }

    public OrdertableDTO(Integer id, Integer userId, String orderCode, OrderStatus status,
            LocalDateTime orderDate, double totalAmount, PaymentMethod paymentMethod, 
            String deliAddress, String comment) {
        this.id = id;
        this.userId = userId;
        this.orderCode = orderCode;
        this.status = status;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.deliAddress = deliAddress;
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderlineDTO> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderlineDTO> orderLines) {
        this.orderLines = orderLines;
    }

    public String getDeliAddress() {
        return deliAddress;
    }

    public void setDeliAddress(String deliAddress) {
        this.deliAddress = deliAddress;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "OrdertableDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderCode='" + orderCode + '\'' +
                ", status=" + status +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                ", orderLinesCount=" + (orderLines != null ? orderLines.size() : 0) +
                ", comment='" + comment + '\'' +
                '}';
    }
}
