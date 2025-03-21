package com.foodmanagement.foodmanagement.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.foodmanagement.foodmanagement.dto.CategoryDTO;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;
import com.foodmanagement.foodmanagement.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // GET: Return a list of categories as List<Map<String, Object>>
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getCategories(
            @RequestParam(required = false) String sort) {

        List<CategoryDTO> dtoList = categoryService.getAllCategories(sort);
        List<Map<String, Object>> response = new ArrayList<>();

        // Convert each CategoryDTO to Map<String, Object>
        for (CategoryDTO dto : dtoList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dto.getId());
            map.put("name", dto.getName());
            map.put("description", dto.getDescription());
            map.put("image", dto.getImage());
            
            // Format the dates
            if (dto.getModifiedDate() != null) {
                map.put("dateModified", dto.getModifiedDate().toLocalDate().toString());
                map.put("time", dto.getModifiedDate().format(DateTimeFormatter.ofPattern("hh:mm a")));
            }
            
            map.put("itemCount", dto.getFoodCount());
            
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    // GET: Return a single category by ID as Map<String, Object>
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Integer id) {
        try {
            CategoryDTO dto = categoryService.getCategoryById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("id", dto.getId());
            response.put("name", dto.getName());
            response.put("description", dto.getDescription());
            response.put("image", dto.getImage());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST: Create a new category (multipart form: 'categoryDTO' fields + 'image')
    @PostMapping
    public ResponseEntity<Map<String, Object>> addCategory(
            @RequestPart(required = false) String name,
            @RequestPart(required = false) String description,
            @RequestPart(required = false) MultipartFile image) {
        try {
            // Build a CategoryDTO from request parts
            CategoryDTO dto = new CategoryDTO();
            dto.setName(name);
            dto.setDescription(description);

            // Call service
            CategoryDTO savedDto = categoryService.createCategory(dto, image);

            // Convert savedDto to map for the response
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedDto.getId());
            response.put("name", savedDto.getName());
            response.put("description", savedDto.getDescription());
            response.put("image", savedDto.getImage());
            // Optionally set date/time
            response.put("dateModified", LocalDate.now().toString());
            response.put("time", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
            response.put("itemCount", 0);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT: Update an existing category
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Integer id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image) {
        try {
            // Build a CategoryDTO from request parameters
            CategoryDTO dto = new CategoryDTO();
            dto.setName(name);
            dto.setDescription(description);

            CategoryDTO updatedDto = categoryService.updateCategory(id, dto, image);

            // Convert updatedDto to map for the response
            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedDto.getId());
            response.put("name", updatedDto.getName());
            response.put("description", updatedDto.getDescription());
            response.put("image", updatedDto.getImage());
            // Optionally set date/time
            response.put("dateModified", LocalDate.now().toString());
            response.put("time", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
            response.put("itemCount", 10);

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE: Remove a category
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Integer id) {
        try {
            categoryService.deleteCategory(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Category deleted successfully");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}