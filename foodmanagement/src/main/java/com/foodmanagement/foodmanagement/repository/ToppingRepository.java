package com.foodmanagement.foodmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodmanagement.foodmanagement.entity.Topping;

public interface ToppingRepository extends JpaRepository<Topping, Integer> {
    @Query("SELECT COUNT(f) FROM Food f JOIN f.toppings t WHERE t.id = :toppingId")
    Long countFoodsByToppingId(@Param("toppingId") Integer toppingId);
}