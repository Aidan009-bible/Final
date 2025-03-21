package com.foodmanagement.foodmanagement.service;

import com.foodmanagement.foodmanagement.dto.UsersDTO;
import com.foodmanagement.foodmanagement.dto.FoodDTO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

@Service
public interface UserProfileService {
    UsersDTO getUserProfile(String email) throws ResourceNotFoundException;
    UsersDTO updateUserProfile(String email, UsersDTO userDTO, MultipartFile image) throws ResourceNotFoundException;
    UsersDTO updateProfileImage(String email, MultipartFile image) throws ResourceNotFoundException;
    List<FoodDTO> getUserFavorites(String email) throws ResourceNotFoundException;
    void addFavorite(String email, Long foodId) throws ResourceNotFoundException;
    void removeFavorite(String email, Long foodId) throws ResourceNotFoundException;
    UsersDTO updatePreferences(String email, Map<String, Object> preferences) throws ResourceNotFoundException;
    void changePassword(String email, String currentPassword, String newPassword) throws ResourceNotFoundException;
    
}