package com.foodmanagement.foodmanagement.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.foodmanagement.foodmanagement.dto.FoodDTO;
import com.foodmanagement.foodmanagement.entity.Category;
import com.foodmanagement.foodmanagement.entity.Food;
import com.foodmanagement.foodmanagement.entity.Orderline;
import com.foodmanagement.foodmanagement.entity.Topping;
import com.foodmanagement.foodmanagement.exception.FoodDeletionException;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;
import com.foodmanagement.foodmanagement.repository.CategoryRepository;
import com.foodmanagement.foodmanagement.repository.FoodRepository;
import com.foodmanagement.foodmanagement.repository.FoodSuggestionRepository;
import com.foodmanagement.foodmanagement.repository.OrderlineRepository;
import com.foodmanagement.foodmanagement.repository.ToppingRepository;
import com.foodmanagement.foodmanagement.service.FoodService;

@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ToppingRepository toppingRepository;

    @Autowired
    private OrderlineRepository orderlineRepository;

    @Autowired
    private FoodSuggestionRepository foodSuggestionRepository;

    @Override
    public FoodDTO createFood(FoodDTO dto, MultipartFile image) throws IOException, ResourceNotFoundException {
        Food food = convertToEntity(dto);

        if (image != null && !image.isEmpty()) {
            food.setImage(image.getBytes());
        }
        
        LocalDateTime now = LocalDateTime.now();
        food.setCreatedDate(now);
        food.setModifiedDate(now);
        Food savedFood = foodRepository.save(food);
        return convertToDTO(savedFood);
    }

    @Override
    public FoodDTO getFoodById(Integer id) throws ResourceNotFoundException {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with ID: " + id));
        return convertToDTO(food);
    }

    @Override
    public List<FoodDTO> getAllFoods(Integer categoryId, String sort, Double priceMin, Double priceMax) {
        List<Food> foodList;

        if (categoryId != null) {
            foodList = foodRepository.findByCategoryId(categoryId);
        } else {
            foodList = foodRepository.findAll();
        }

        // Apply price range filtering
        if (priceMin != null || priceMax != null) {
            foodList = foodList.stream()
                    .filter(food -> {
                        if (priceMin != null && priceMax != null) {
                            return food.getPrice() >= priceMin && food.getPrice() <= priceMax;
                        } else if (priceMin != null) {
                            return food.getPrice() >= priceMin;
                        } else {
                            return food.getPrice() <= priceMax;
                        }
                    })
                    .collect(Collectors.toList());
        }

        // Sorting logic
        if (sort != null) {
            switch (sort.toLowerCase()) {
                case "name":
                    foodList.sort(Comparator.comparing(Food::getTitle));
                    break;
                case "price":
                    foodList.sort(Comparator.comparing(Food::getPrice));
                    break;
                case "category":
                    foodList.sort(
                            Comparator.comparing(food -> food.getCategory() != null ? food.getCategory().getName() : "",
                                    Comparator.nullsLast(Comparator.naturalOrder())));
                    break;
                case "stock":
                    foodList.sort(Comparator.comparing(food -> food.getStock() != null ? food.getStock() : 0,
                            Comparator.nullsLast(Comparator.naturalOrder())));
                    break;
                case "newest":
                    foodList.sort(Comparator.comparing(Food::getCreatedDate,
                            Comparator.nullsLast(Comparator.reverseOrder())));
                    break;
                case "rating":
                    foodList.sort(Comparator.comparing(food -> food.getRating() != null ? food.getRating() : 0.0,
                            Comparator.reverseOrder()));
                    break;
                case "popularity":
                    foodList.sort(Comparator.comparing(food -> food.getPopularity() != null ? food.getPopularity() : 0,
                            Comparator.reverseOrder()));
                    break;
                case "discount":
                    foodList.sort(Comparator.comparing(food -> food.getDiscount() != null ? food.getDiscount() : 0.0,
                            Comparator.reverseOrder()));
                    break;
                case "default":
                    break;
                default:
                    throw new IllegalArgumentException("Invalid sort parameter: " + sort);
            }
        }

        return foodList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public FoodDTO updateFood(Integer id, FoodDTO dto) throws ResourceNotFoundException {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with ID: " + id));

        existingFood.setTitle(dto.getTitle());
        existingFood.setPrice(dto.getPrice());
        existingFood.setDescription(dto.getDescription());
        existingFood.setStock(dto.getStock());
        existingFood.setRating(dto.getRating());
        existingFood.setIsAvailable(dto.getIsAvailable());
        existingFood.setDiscount(dto.getDiscount());
        existingFood.setModifiedDate(LocalDateTime.now());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            existingFood.setCategory(category);
        }

        if (dto.getToppingIds() != null) {
            Set<Topping> toppings = toppingRepository.findAllById(dto.getToppingIds())
                    .stream().collect(Collectors.toSet());
            existingFood.setToppings(toppings);
        }

        Food updatedFood = foodRepository.save(existingFood);
        return convertToDTO(updatedFood);
    }

    @Override
    @Transactional
    public void deleteFood(Integer id) throws ResourceNotFoundException {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with ID: " + id));
        
        try {
            // 1. Check for existing orders
            List<Orderline> orderlines = orderlineRepository.findByFood_Id(id);
            if (!orderlines.isEmpty()) {
                throw new FoodDeletionException("Cannot delete food that has been ordered. It has " + orderlines.size() + " orders.");
            }
            
            // 2. Remove food suggestions where this food is the source
            foodSuggestionRepository.deleteAll(
                foodSuggestionRepository.findByFoodIdOrderByPurchaseCountDesc(id)
            );
            
            // 3. Remove food suggestions where this food is the suggested food
            foodSuggestionRepository.findByFoodIdAndSuggestedFoodId(null, id)
                .ifPresent(suggestion -> foodSuggestionRepository.delete(suggestion));
            
            // 4. Clear topping associations
            food.getToppings().clear();
            foodRepository.save(food);
            
            // 5. Finally delete the food
            foodRepository.delete(food);
        } catch (Exception e) {
            throw new FoodDeletionException("Error deleting food: " + e.getMessage());
        }
    }

    @Override
    public List<FoodDTO> getSuggestedFoods(Integer foodId) throws ResourceNotFoundException {
        Food currentFood = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        return foodRepository.findTop3ByCategoryAndIdNotOrderByPopularityDesc(
                currentFood.getCategory(), foodId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List getTopSellingFoods() {
        return foodRepository.findTop5ByOrderByTotalSaleQuantityDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Food convertToEntity(FoodDTO dto) throws ResourceNotFoundException {
        Food food = new Food();
        food.setTitle(dto.getTitle());
        food.setPrice(dto.getPrice());
        food.setDescription(dto.getDescription());
        food.setStock(dto.getStock());
        food.setRating(dto.getRating());
        food.setIsAvailable(dto.getIsAvailable());
        food.setDiscount(dto.getDiscount());
        food.setTotalSaleQuantity(dto.getTotalSaleQuantity());
        food.setPopularity(dto.getPopularity());

        // Category is required, so we must ensure it's set
        Category category;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        } else {
            // Get the default category or throw an error if none exists
            category = categoryRepository.findById(1) // Assuming ID 1 is your default category
                    .orElseThrow(() -> new ResourceNotFoundException("Default category not found"));
        }
        food.setCategory(category);

        if (dto.getToppingIds() != null) {
            Set<Topping> toppings = toppingRepository.findAllById(dto.getToppingIds())
                    .stream().collect(Collectors.toSet());
            food.setToppings(toppings);
        }

        return food;
    }

    private FoodDTO convertToDTO(Food food) {
        FoodDTO dto = new FoodDTO();
        dto.setId(food.getId());
        dto.setTitle(food.getTitle());
        dto.setPrice(food.getPrice());
        dto.setDescription(food.getDescription());
        dto.setStock(food.getStock());
        dto.setRating(food.getRating());
        dto.setIsAvailable(food.getIsAvailable());
        dto.setCreatedDate(food.getCreatedDate());
        dto.setModifiedDate(food.getModifiedDate());
        dto.setModifiedTime(food.getModifiedDate());
        dto.setTotalSaleQuantity(food.getTotalSaleQuantity());
        dto.setPopularity(food.getPopularity());
        dto.setDiscount(food.getDiscount() != null ? food.getDiscount() : 0.0);

        if (food.getCategory() != null) {
            dto.setCategoryId(food.getCategory().getId());
            dto.setCategory(food.getCategory().getName());
        } else {
            dto.setCategory("Uncategorized");
        }

        if (food.getImage() != null && food.getImage().length > 0) {
            String base64Image = Base64.getEncoder().encodeToString(food.getImage());
            dto.setImage("data:image/*;base64," + base64Image);
        }

        if (food.getToppings() != null) {
            dto.setToppingIds(food.getToppings().stream().map(Topping::getId).collect(Collectors.toSet()));
        }

        return dto;
    }

    public Optional<Food> geTFoodById(Integer id) {
        return foodRepository.findById(id);
    }

}