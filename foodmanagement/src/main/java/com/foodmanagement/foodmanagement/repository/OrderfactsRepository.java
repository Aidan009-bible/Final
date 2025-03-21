package com.foodmanagement.foodmanagement.repository;

import com.foodmanagement.foodmanagement.entity.Orderfacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderfactsRepository extends JpaRepository<Orderfacts, Integer> {
    // Basic CRUD operations are provided by JpaRepository
} 