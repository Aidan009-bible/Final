package com.foodmanagement.foodmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodmanagement.foodmanagement.entity.Users;

@Repository
public interface AdminRepository extends JpaRepository<Users, Integer> {
    // Basic CRUD operations are automatically provided by JpaRepository
}