package com.foodmanagement.foodmanagement.repository;

import com.foodmanagement.foodmanagement.entity.Users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.util.List;

//import java.time.LocalDate;
//import java.util.List;

//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;

//import java.util.Map;

//import java.util.HashMap;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByemail(String email);
    // List<Orderline> findByUserID(int userID); // Use the correct column name

    Optional<Users> findById(Integer id);

    // List<Orderline> findByStatus(String status);

    // Add any custom query methods you need
}