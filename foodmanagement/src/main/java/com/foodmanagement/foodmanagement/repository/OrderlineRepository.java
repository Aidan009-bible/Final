package com.foodmanagement.foodmanagement.repository;

import com.foodmanagement.foodmanagement.entity.Orderline;
import com.foodmanagement.foodmanagement.entity.OrderlineId;

import org.springframework.stereotype.Repository;

//import java.util.List;

//import java.time.LocalDate;
//import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;

//import java.util.Map;

//import java.util.HashMap;

import java.util.List;

@Repository
public interface OrderlineRepository extends JpaRepository<Orderline, OrderlineId> {
    List<Orderline> findByOrder_Id(Integer orderId);

    List<Orderline> findByFood_Id(Integer foodId);

    // List<Orderline> findByUserID(int userID); // Use the correct column name

    // List<Orderline> findByStatus(String status);

}