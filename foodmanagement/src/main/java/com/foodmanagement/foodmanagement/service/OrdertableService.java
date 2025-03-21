package com.foodmanagement.foodmanagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodmanagement.foodmanagement.entity.Ordertable;
import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;
import com.foodmanagement.foodmanagement.entity.enums.PaymentMethod;
import com.foodmanagement.foodmanagement.repository.OrdertableRepository;
import com.foodmanagement.foodmanagement.dto.UsersDTO;
import com.foodmanagement.foodmanagement.service.UserProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Arrays;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Service
public class OrdertableService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OrdertableRepository orderTableRepository;

    @Autowired
    private UserProfileService userProfileService;

    public List<Ordertable> getAllOrders() {
        List<Ordertable> orders = orderTableRepository.findAll();

        return orders;
    }

    public int getOrderCountByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);

        // Convert LocalDate to LocalDateTime range for filtering by the full day
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

        // Use JPQL query instead of native SQL
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(o) FROM Ordertable o WHERE o.orderDate BETWEEN :start AND :end", Long.class);
        query.setParameter("start", startOfDay);
        query.setParameter("end", endOfDay);

        Long count = query.getSingleResult();
        return count.intValue();
    }

    public String getOrderStatus(int orderId) {
        // Fetch the order using existing repository method (which you don't want to
        // modify)
        Ordertable order = orderTableRepository.findById(orderId).orElse(null);

        // Check if the order exists
        if (order != null) {
            // Return the status of the order as a string
            return order.getStatus().toString();
        } else {
            // If the order is not found, handle accordingly
            return "Order not found";
        }
    }

    // Adjusting the method to use LocalDateTime for filtering by dates
    public List<Ordertable> getOrders(PaymentMethod paymentMethod, OrderStatus status, String startDate,
            String endDate) {
        try {
            List<Ordertable> orders = orderTableRepository.findAll();

            // Filter logic...
            if (paymentMethod != null) {
                orders = orders.stream()
                        .filter(order -> order.getPaymentMethod() != null
                                && paymentMethod.equals(order.getPaymentMethod()))
                        .collect(Collectors.toList());
            }

            if (status != null) {
                orders = orders.stream()
                        .filter(order -> order.getStatus() != null && status.equals(order.getStatus()))
                        .collect(Collectors.toList());
            }

            // Date filtering...
            return orders;
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // public double calculateTotalSales() {
    // return orderLineRepository.findAll()
    // .stream()
    // .mapToDouble(Orderline::getQuantity) // Assuming there's a getTotalPrice()
    // method
    // .sum();
    // }
    public Ordertable updateOrder(Ordertable order) {
        return orderTableRepository.save(order);
    }
    // Get order by ID
    public Optional<Ordertable> getOrderById(Integer orderId) {
        return orderTableRepository.findById(orderId);
    }

    // Save or update order
    public Ordertable saveOrder(Ordertable order) {
        return orderTableRepository.save(order);
    }

    public int getOrderCountByDateAndStatus(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status) {
        return orderTableRepository.countByOrderDateBetweenAndStatus(startDate, endDate, status);
    }

    public int getOrderCountByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return orderTableRepository.countByOrderDateBetween(startDate, endDate);
    }

    public int getOrderCountByStatus(OrderStatus status) {
        return orderTableRepository.countByStatus(status);
    }

    public int getTotalOrderCount() {
        return (int) orderTableRepository.count();
    }

    public int getOrderCountByDate(LocalDate date) {
        return orderTableRepository.countByOrderDate(date);
    }

    public int getOrderCountByDateAndStatus(LocalDate date, OrderStatus status) {
        return orderTableRepository.countByOrderDateAndStatus(date, status);
    }

    public double getTotalSalesByMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // Convert LocalDate to LocalDateTime
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return orderTableRepository.sumTotalByOrderDateBetween(startDateTime, endDateTime);
    }

    public double getTotalSalesByYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // Convert LocalDate to LocalDateTime
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return orderTableRepository.sumTotalByOrderDateBetween(startDateTime, endDateTime);
    }

    public double getTotalSalesByDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return orderTableRepository.sumTotalByOrderDateBetween(startDateTime, endDateTime);
    }

    public double getTotalSales() {
        return orderTableRepository.findAll().stream()
                .mapToDouble(order -> order.getTotalAmount())
                .sum();
    }

    @Transactional

    public Ordertable createOrder(Ordertable order) {

        return orderTableRepository.save(order);

    }

    public List<Ordertable> getOrdersByUserId(Integer userId) {
        return orderTableRepository.findAll().stream()
                .filter(order -> order.getUser().getId() == userId)
                .collect(Collectors.toList());
    }

    public List<Ordertable> getCurrentOrdersByUserEmail(String email) {
        try {
            UsersDTO user = userProfileService.getUserProfile(email);
            return orderTableRepository.findByUserIdAndStatusNotIn(
                user.getId(), 
                Arrays.asList(OrderStatus.DELIVERED, OrderStatus.CANCELLED)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Ordertable> getOrderHistoryByUserEmail(String email) {
        try {
            UsersDTO user = userProfileService.getUserProfile(email);
            return orderTableRepository.findByUserIdAndStatusIn(
                user.getId(),
                Arrays.asList(OrderStatus.DELIVERED, OrderStatus.CANCELLED)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public Ordertable getOrderByIdAndUserEmail(Integer orderId, String userEmail) {
        return orderTableRepository.findByIdAndUser_Email(orderId, userEmail)
            .orElse(null);
    }
}
