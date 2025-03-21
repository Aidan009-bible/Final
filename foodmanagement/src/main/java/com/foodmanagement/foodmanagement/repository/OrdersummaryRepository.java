package com.foodmanagement.foodmanagement.repository;

import com.foodmanagement.foodmanagement.entity.Ordersummary;
import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;
import com.foodmanagement.foodmanagement.entity.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface OrdersummaryRepository extends JpaRepository<Ordersummary, Integer> {
    List<Ordersummary> findByStatus(OrderStatus status);

    List<Ordersummary> findByPaymentMethod(PaymentMethod paymentMethod);

    List<Ordersummary> findByName(String name);

    List<Ordersummary> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Ordersummary> findByOrderDateBetweenAndStatus(
            LocalDateTime startDate,
            LocalDateTime endDate,
            OrderStatus status);
}