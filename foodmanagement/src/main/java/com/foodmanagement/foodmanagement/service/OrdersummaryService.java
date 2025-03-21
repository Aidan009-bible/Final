package com.foodmanagement.foodmanagement.service;

import com.foodmanagement.foodmanagement.entity.Ordersummary;
import com.foodmanagement.foodmanagement.repository.OrdersummaryRepository;
import com.foodmanagement.foodmanagement.dto.OrdersummaryDTO;
import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;
import com.foodmanagement.foodmanagement.entity.enums.PaymentMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
public class OrdersummaryService {

    @Autowired
    private OrdersummaryRepository ordersummaryRepository;

    // Add method to generate order code if needed
    private String generateOrderCode(Integer orderId) {
        return String.format("#%06d", orderId); // Creates format like #000001
    }

    // Convert Entity to DTO
    private OrdersummaryDTO convertToDTO(Ordersummary ordersummary) {
        return new OrdersummaryDTO(
                ordersummary.getOrderId(),
                ordersummary.getOrderCode(),
                ordersummary.getOrderDate(),
                ordersummary.getName(),
                ordersummary.getPaymentMethod(),
                ordersummary.getFoodTitle(),
                ordersummary.getPrice(),
                ordersummary.getTotalAmount(),
                ordersummary.getQuantity(),
                ordersummary.getStatus());
    }

    // Get all order summaries
    public List<Ordersummary> getAllOrderSummaries() {
        return ordersummaryRepository.findAll();
    }

    // Get order summary by ID
    public Optional<Ordersummary> getOrderSummaryById(Integer orderId) {
        return ordersummaryRepository.findById(orderId);
    }

    // Get order summaries by status
    public List<OrdersummaryDTO> getOrderSummariesByStatus(String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return ordersummaryRepository.findByStatus(orderStatus)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status: " + status);
            return List.of();
        }
    }

    // Get order summaries by payment method
    public List<OrdersummaryDTO> getOrderSummariesByPaymentMethod(String paymentMethod) {
        try {
            PaymentMethod method = PaymentMethod.valueOf(paymentMethod.toUpperCase());
            return ordersummaryRepository.findByPaymentMethod(method)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid payment method: " + paymentMethod);
            return List.of();
        }
    }

    // Save or update order summary
    public Ordersummary saveOrderSummary(Ordersummary ordersummary) {
        if (ordersummary.getOrderCode() == null) {
            ordersummary.setOrderCode(generateOrderCode(ordersummary.getOrderId()));
        }
        return ordersummaryRepository.save(ordersummary);
    }

    // Get orders by date
    public List<Ordersummary> getOrderSummariesByDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

            return ordersummaryRepository.findByOrderDateBetween(startOfDay, endOfDay);
        } catch (Exception e) {
            System.err.println("Error getting orders by date: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get orders by date and status
    public List<Ordersummary> getOrderSummariesByDateAndStatus(String date, OrderStatus status) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

            return ordersummaryRepository.findByOrderDateBetweenAndStatus(startOfDay, endOfDay, status);
        } catch (Exception e) {
            System.err.println("Error getting orders by date and status: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Ordersummary> getOrderSummariesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return ordersummaryRepository.findByOrderDateBetween(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error getting orders by date range: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}