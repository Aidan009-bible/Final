package com.foodmanagement.foodmanagement.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//import java.util.List;

//import java.time.LocalDate;
//import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodmanagement.foodmanagement.entity.Ordertable;
import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;

//import java.util.Map;

//import java.util.HashMap;

@Repository
public interface OrdertableRepository extends JpaRepository<Ordertable, Integer> {
    // List<Orderline> findByUserID(int userID); // Use the correct column name

    // List<Orderline> findByStatus(String status);
    int countByOrderDateBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status);

    int countByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    int countByStatus(OrderStatus status);

    int countByOrderDate(LocalDate orderDate);

    int countByOrderDateAndStatus(LocalDate orderDate, OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Ordertable o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    double sumTotalByOrderDateBetween(
            @Param("startDate") LocalDateTime startDateTime,
            @Param("endDate") LocalDateTime endDateTime);

    @Query("SELECT o FROM Ordertable o WHERE o.user.id = :userId AND o.status NOT IN :statuses")
    List<Ordertable> findByUserIdAndStatusNotIn(@Param("userId") Integer userId, @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT o FROM Ordertable o WHERE o.user.id = :userId AND o.status IN :statuses")
    List<Ordertable> findByUserIdAndStatusIn(@Param("userId") Integer userId, @Param("statuses") List<OrderStatus> statuses);
    Optional<Ordertable> findByIdAndUser_Email(Integer id, String email);
}