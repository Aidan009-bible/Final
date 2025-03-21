package com.foodmanagement.foodmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Data
@Entity
@Immutable  // This indicates it's a read-only view
@Table(name = "order_facts")
public class Orderfacts {
    
    @Id
    @Column(name = "total_orders")
    private Integer totalOrders;  // Using totalOrders as the ID since we only have one record
    
    @Column(name = "total_quantity")
    private Long totalQuantity;
    
    @Column(name = "total_sales")
    private Double totalSales;
    
    @Column(name = "total_users")
    private Integer totalUsers;
} 