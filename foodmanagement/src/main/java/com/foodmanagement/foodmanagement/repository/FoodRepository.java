package com.foodmanagement.foodmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodmanagement.foodmanagement.entity.Category;
import com.foodmanagement.foodmanagement.entity.Food;
import com.foodmanagement.foodmanagement.entity.Topping;

public interface FoodRepository extends JpaRepository<Food, Integer> {
  List<Food> findByCategoryId(Integer categoryId);
  List<Food> findTop3ByCategoryAndIdNotOrderByPopularityDesc(Category category, Integer id);

  @Query("SELECT t FROM Food f JOIN f.toppings t WHERE f.id = :foodId")
  List<Topping> findToppingsByFoodId(@Param("foodId") Integer foodId);

  List<Food> findTop5ByOrderByTotalSaleQuantityDesc();
}