package com.foodmanagement.foodmanagement.entity;

//import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;
import com.foodmanagement.foodmanagement.entity.enums.PaymentMethod;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
//import java.math.BigDecimal; // Import BigDecimal
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordertable")
public class Ordertable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "order_code", nullable = false, unique = true)
    private String orderCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enum('PENDING','PREPARING','READY','DELIVERED','CANCELLED') default 'PENDING'")
    private OrderStatus status;

    @Column(name = "order_date", nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, columnDefinition = "double default 0")
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, columnDefinition = "enum('CASH','CARD','ONLINE')")
    private PaymentMethod paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Orderline> orderLines;

    @Column(name = "deli_address")
    private String deliAddress;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    // Default constructor
    public Ordertable() {
        // Set default order date to current time when creating new order
        this.orderDate = LocalDateTime.now();
    }

    // Update the constructor to include comment
    public Ordertable(Users user, String orderCode, OrderStatus status, LocalDateTime orderDate,
            double totalAmount, PaymentMethod paymentMethod, String deliAddress, String comment) {
        this.user = user;
        this.orderCode = orderCode;
        this.status = status;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.deliAddress = deliAddress;
        this.comment = comment;
    }

    // Constructor with all fields
    public Ordertable(Users user, String orderCode, OrderStatus status, LocalDateTime orderDate,
            double totalAmount, PaymentMethod paymentMethod, String deliAddress) {
        this.user = user;
        this.orderCode = orderCode;
        this.status = status;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.deliAddress = deliAddress;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
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
        // Ensure we never return null
        return orderDate != null ? orderDate : LocalDateTime.now();
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate != null ? orderDate : LocalDateTime.now();
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

    public List<Orderline> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<Orderline> orderLines) {
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
        return "Ordertable{" +
                "id=" + id +
                ", orderCode='" + orderCode + '\'' +
                ", status=" + status +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                ", comment='" + comment + '\'' +
                '}';
    }
}
