package com.foodmanagement.foodmanagement.service;

import java.io.IOException;
import java.util.List;
//import java.util.Map;
import java.util.Optional;
//import java.util.stream.Collectors;
//import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

import com.foodmanagement.foodmanagement.dto.FoodDTO;
import com.foodmanagement.foodmanagement.entity.Food;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;

public interface FoodService {
    List<FoodDTO> getAllFoods(Integer categoryId, String sort, Double priceMin, Double priceMax);

    FoodDTO getFoodById(Integer id) throws ResourceNotFoundException;

    FoodDTO createFood(FoodDTO foodDTO, MultipartFile image) throws ResourceNotFoundException, IOException;

    FoodDTO updateFood(Integer id, FoodDTO foodDTO) throws ResourceNotFoundException;

    void deleteFood(Integer id) throws ResourceNotFoundException;

    List<FoodDTO> getSuggestedFoods(Integer foodId) throws ResourceNotFoundException;

    List<FoodDTO> getTopSellingFoods();

    public Optional<Food> geTFoodById(Integer id);
}
