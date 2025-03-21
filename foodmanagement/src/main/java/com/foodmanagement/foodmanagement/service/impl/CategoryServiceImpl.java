package com.foodmanagement.foodmanagement.service.impl;

import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foodmanagement.foodmanagement.dto.CategoryDTO;
import com.foodmanagement.foodmanagement.entity.Category;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;
import com.foodmanagement.foodmanagement.repository.CategoryRepository;
import com.foodmanagement.foodmanagement.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDTO> getAllCategories(String sort) {
        List<Category> categories = categoryRepository.findAll();

        // Sorting logic
        if (sort != null) {
            switch (sort.toLowerCase()) {
                case "name":
                    categories.sort(Comparator.comparing(Category::getName));
                    break;
                case "date":
                    categories.sort(Comparator.comparing(Category::getModifiedDate, 
                        Comparator.nullsLast(Comparator.reverseOrder())));
                    break;
                case "itemcount":
                    categories.sort(Comparator.comparing(category -> 
                        category.getFoods() != null ? category.getFoods().size() : 0,
                        Comparator.reverseOrder()));
                    break;
                default:
                    // Default sorting by modified date
                    categories.sort(Comparator.comparing(Category::getModifiedDate, 
                        Comparator.nullsLast(Comparator.reverseOrder())));
                    break;
            }
        }

        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Integer id) throws ResourceNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToDTO(category);
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO, MultipartFile image) {
        Category category = convertToEntity(categoryDTO);

        // If an image is provided, store it as raw bytes
        if (image != null && !image.isEmpty()) {
            try {
                category.setImage(image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to process image", e);
            }
        }

        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Override
    public CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO, MultipartFile image)
            throws ResourceNotFoundException {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Update basic fields
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        // If a new image is provided, overwrite the old one
        if (image != null && !image.isEmpty()) {
            try {
                existingCategory.setImage(image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to process image", e);
            }
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Integer id) throws ResourceNotFoundException {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    // ------------------------------------------
    // Helper Conversion Methods
    // ------------------------------------------
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setCreatedDate(category.getCreatedDate());
        dto.setModifiedDate(category.getModifiedDate());

        // Convert stored bytes to Base64 for the frontend
        if (category.getImage() != null && category.getImage().length > 0) {
            String base64Image = Base64.getEncoder().encodeToString(category.getImage());
            dto.setImage("data:image/*;base64," + base64Image);
        }

        // Calculate food count
        if (category.getFoods() != null) {
            dto.setFoodCount(category.getFoods().size());
        } else {
            dto.setFoodCount(0);
        }

        return dto;
    }

    private Category convertToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        
        // Don't set dates here - they're handled by @PrePersist and @PreUpdate
        
        return category;
    }
}