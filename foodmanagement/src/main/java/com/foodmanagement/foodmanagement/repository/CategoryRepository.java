package com.foodmanagement.foodmanagement.repository;

import com.foodmanagement.foodmanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}