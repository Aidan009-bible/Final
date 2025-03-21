package com.foodmanagement.foodmanagement.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.foodmanagement.foodmanagement.dto.CategoryDTO;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;

public interface CategoryService {
    List<CategoryDTO> getAllCategories(String sort);
    
    CategoryDTO getCategoryById(Integer id) throws ResourceNotFoundException;
    
    CategoryDTO createCategory(CategoryDTO categoryDTO, MultipartFile image) throws ResourceNotFoundException;
    
    CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO, MultipartFile image) throws ResourceNotFoundException;
    
    void deleteCategory(Integer id) throws ResourceNotFoundException;
}