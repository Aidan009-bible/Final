package com.foodmanagement.foodmanagement.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodmanagement.foodmanagement.dto.FoodDTO;
import com.foodmanagement.foodmanagement.dto.ToppingDTO;
import com.foodmanagement.foodmanagement.entity.Food;
import com.foodmanagement.foodmanagement.exception.FoodDeletionException;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;
import com.foodmanagement.foodmanagement.repository.FoodRepository;
import com.foodmanagement.foodmanagement.service.FoodService;
import com.foodmanagement.foodmanagement.service.FoodSuggestionService;
import com.foodmanagement.foodmanagement.service.ToppingService;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin("*")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private ToppingService toppingService;

    @Autowired
    private FoodSuggestionService foodSuggestionService;

    @Autowired
    private FoodRepository foodRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createFood(
            @RequestPart(required = false) String title,  // Still receiving "title"
            @RequestPart(required = false) String price,  
            @RequestPart(required = false) String description,
            @RequestPart(required = false) String stock,  
            @RequestPart(required = false) String rating, 
            @RequestPart(required = false) String discount, 
            @RequestPart(required = false) String isAvailable, 
            @RequestPart(required = false) String categoryId,  
            @RequestPart(required = false) String toppings,  // Add this parameter
            @RequestPart(required = false) MultipartFile image) {

        try {
            // Convert String inputs to proper types safely
            Double priceValue = parseDouble(price);
            Double discountValue = parseDouble(discount);
            Integer stockValue = parseInteger(stock);
            Double ratingValue = parseDouble(rating);
            Boolean isAvailableValue = parseBoolean(isAvailable);
            Integer categoryIdValue = parseInteger(categoryId);

            // Validate required fields
            if (title == null || title.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
            }
            if (priceValue == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Price must be a valid number"));
            }
            if (categoryIdValue == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Category ID must be a valid integer"));
            }

            // Build DTO
            FoodDTO dto = new FoodDTO();
            dto.setTitle(title); // Changed from setName to setTitle to match database column
            dto.setPrice(priceValue);
            dto.setDescription(description);
            dto.setStock(stockValue);
            dto.setDiscount(discountValue);
            dto.setRating(ratingValue != null ? ratingValue : 0.0);
            dto.setIsAvailable(isAvailableValue != null ? isAvailableValue : true);
            dto.setCategoryId(categoryIdValue);
            
            // Set default values for required fields
            dto.setPopularity(0);
            dto.setTotalSaleQuantity(0);
            dto.setCreatedDate(LocalDateTime.now());
            dto.setModifiedDate(LocalDateTime.now());
            dto.setModifiedTime(LocalDateTime.now());
       

            // Convert comma-separated string to Set<Integer>
            if (toppings != null && !toppings.isEmpty()) {
                Set<Integer> toppingIds = Arrays.stream(toppings.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
                dto.setToppingIds(toppingIds);
            }

            // Call service
            FoodDTO savedFood = foodService.createFood(dto, image);

            // Build Response
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedFood.getId());
            response.put("title", savedFood.getTitle()); // Changed from getName to getTitle
            response.put("price", savedFood.getPrice());
            response.put("description", savedFood.getDescription());
            response.put("discount", savedFood.getDiscount());
            response.put("stock", savedFood.getStock());
            response.put("rating", savedFood.getRating());
            response.put("isAvailable", savedFood.getIsAvailable());
            response.put("categoryId", savedFood.getCategoryId());
            response.put("image", savedFood.getImage());
            response.put("dateModified", savedFood.getModifiedDate().toString());
            response.put("time", savedFood.getModifiedTime().format(DateTimeFormatter.ofPattern("hh:mm a")));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFoodById(@PathVariable Integer id) {
        try {
            FoodDTO food = foodService.getFoodById(id);
            Map<String, Object> foodMap = new HashMap<>();
            foodMap.put("id", food.getId());
            foodMap.put("title", food.getTitle());
            foodMap.put("price", food.getPrice());
            foodMap.put("stock", food.getStock());
            foodMap.put("discount", food.getDiscount());
            foodMap.put("description", food.getDescription());
            foodMap.put("rating", food.getRating());
            foodMap.put("image", food.getImage());
            foodMap.put("isAvailable", food.getIsAvailable());
            
            // Fetch topping details for this food
            List<Map<String, Object>> toppings = food.getToppingIds().stream()
                .map(toppingId -> {
                    try {
                        ToppingDTO topping = toppingService.getToppingById(toppingId);
                        Map<String, Object> toppingMap = new HashMap<>();
                        toppingMap.put("id", topping.getId());
                        toppingMap.put("name", topping.getName());
                        toppingMap.put("price", topping.getPrice());
                        toppingMap.put("isAvailable", topping.getIsAvailable());
                        return toppingMap;
                    } catch (ResourceNotFoundException e) {
                        return null;
                    }
                })
                .filter(t -> t != null)
                .collect(Collectors.toList());
            
            foodMap.put("toppings", toppings);
            return ResponseEntity.ok(foodMap);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllFoods(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax) {
        
        List<FoodDTO> foods = foodService.getAllFoods(categoryId, sort, priceMin, priceMax);
        
        List<Map<String, Object>> response = foods.stream().map(food -> {
            Map<String, Object> foodMap = new HashMap<>();
            foodMap.put("id", food.getId());
            foodMap.put("title", food.getTitle());
            foodMap.put("description", food.getDescription());
            foodMap.put("price", food.getPrice());
            foodMap.put("categoryId", food.getCategoryId());
            foodMap.put("category", food.getCategory() != null ? food.getCategory() : "Uncategorized");
            foodMap.put("discount", food.getDiscount() != null ? food.getDiscount() : 0.0);
            foodMap.put("stock", food.getStock());
            foodMap.put("rating", food.getRating());
            foodMap.put("popularity", food.getPopularity());
            foodMap.put("totalSaleQuantity", food.getTotalSaleQuantity());
            foodMap.put("image", food.getImage());
            foodMap.put("dateModified", food.getModifiedDate() != null ? food.getModifiedDate().toLocalDate().toString() : "");
            foodMap.put("time", food.getModifiedTime() != null ? food.getModifiedTime().toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a")) : "");
            
            // Get toppings for this food
            List<Map<String, Object>> toppings = food.getToppingIds().stream().map(toppingId -> {
                try {
                    ToppingDTO topping = toppingService.getToppingById(toppingId);
                    Map<String, Object> toppingMap = new HashMap<>();
                    toppingMap.put("id", topping.getId());
                    toppingMap.put("name", topping.getName());
                    toppingMap.put("price", topping.getPrice());
                    toppingMap.put("isAvailable", topping.getIsAvailable());
                    toppingMap.put("dateModified", LocalDate.now().toString());
                    toppingMap.put("time", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
                    return toppingMap;
                } catch (ResourceNotFoundException e) {
                    return null;
                }
            }).filter(t -> t != null).collect(Collectors.toList());
            
            foodMap.put("toppings", toppings);
            return foodMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateFood(
            @PathVariable Integer id,
            @RequestPart(required = false) String title,
            @RequestPart(required = false) String price,
            @RequestPart(required = false) String description,
            @RequestPart(required = false) String stock,
            @RequestPart(required = false) String discount,
            @RequestPart(required = false) String rating,
            @RequestPart(required = false) String isAvailable,
            @RequestPart(required = false) String categoryId,
            @RequestPart(required = false) String toppings,  // Add this parameter
            @RequestPart(required = false) MultipartFile image) {
        try {
            FoodDTO foodDTO = new FoodDTO();
            foodDTO.setTitle(title);
            foodDTO.setPrice(price != null ? Double.parseDouble(price) : null);
            foodDTO.setDescription(description);
            foodDTO.setStock(stock != null ? Integer.parseInt(stock) : null);
            foodDTO.setDiscount(discount != null ? Double.parseDouble(discount) : 0.0); // Discount is not updatable
            foodDTO.setRating(rating != null ? Double.parseDouble(rating) : null);
            foodDTO.setIsAvailable(isAvailable != null ? Boolean.parseBoolean(isAvailable) : null);
            foodDTO.setCategoryId(categoryId != null ? Integer.parseInt(categoryId) : null);

            // Convert comma-separated string to Set<Integer>
            if (toppings != null && !toppings.isEmpty()) {
                Set<Integer> toppingIds = Arrays.stream(toppings.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
                foodDTO.setToppingIds(toppingIds);
            }

            FoodDTO updatedFood = foodService.updateFood(id, foodDTO);
            // ... rest of your response handling
            return ResponseEntity.ok(Map.of("id", updatedFood.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFood(@PathVariable Integer id) {
        try {
            foodService.deleteFood(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", "Food not found with ID: " + id));
        } catch (FoodDeletionException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "An unexpected error occurred while deleting the food"));
        }
    }

    @GetMapping("/{id}/suggestions")
    public ResponseEntity<List<Map<String, Object>>> getSuggestedFoods(@PathVariable Integer id) throws ResourceNotFoundException {
        List<Food> suggestions = foodSuggestionService.getSuggestedFoods(id);
        
        if (suggestions.isEmpty()) {
            // Fall back to category-based suggestions
            Food currentFood = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));
            suggestions = foodRepository.findTop3ByCategoryAndIdNotOrderByPopularityDesc(
                currentFood.getCategory(), id);
        }
        
        List<Map<String, Object>> response = suggestions.stream()
            .map(food -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", food.getId());
                map.put("title", food.getTitle());
                map.put("price", food.getPrice());
                map.put("image", food.getImage() != null ? 
                    "data:image/*;base64," + Base64.getEncoder().encodeToString(food.getImage()) : 
                    null);
                return map;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-selling")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingFoods() {
        try {
            List<FoodDTO> topFoods = foodService.getTopSellingFoods();
            
            List<Map<String, Object>> response = topFoods.stream().map(food -> {
                Map<String, Object> foodMap = new HashMap<>();
                foodMap.put("id", food.getId());
                foodMap.put("title", food.getTitle());
                foodMap.put("image", food.getImage());
                foodMap.put("soldCount", food.getTotalSaleQuantity());
                return foodMap;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Helper methods for safe parsing
    private Double parseDouble(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Double.valueOf(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Integer.valueOf(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseBoolean(String value) {
        return (value != null && !value.isEmpty()) ? Boolean.valueOf(value) : null;
    }
}

