package com.foodmanagement.foodmanagement.service.impl;

import com.foodmanagement.foodmanagement.dto.UsersDTO;
import com.foodmanagement.foodmanagement.dto.FoodDTO;
import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;
import com.foodmanagement.foodmanagement.entity.Users;
import com.foodmanagement.foodmanagement.repository.UserProfileRepository;
import com.foodmanagement.foodmanagement.repository.UsersRepository;
import com.foodmanagement.foodmanagement.repository.OrdertableRepository;
import com.foodmanagement.foodmanagement.service.UserProfileService;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;
import com.foodmanagement.foodmanagement.exception.InvalidRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.util.Arrays;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private OrdertableRepository ordertableRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UsersDTO getUserProfile(String email) throws ResourceNotFoundException {
        Users user = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        UsersDTO dto = new UsersDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        
        // Handle the profile image
        if (user.getProfileImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getProfileImage());
            dto.setProfileImage("data:image/jpeg;base64," + base64Image);
        }
        
        return dto;
    }

    @Override
    public UsersDTO updateUserProfile(String email, UsersDTO userDTO, MultipartFile image) throws ResourceNotFoundException {
        try {
            Users user = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
            
            // Update user fields if they are not null
            if (userDTO.getName() != null) user.setName(userDTO.getName());
            if (userDTO.getAddress() != null) user.setAddress(userDTO.getAddress());
            if (userDTO.getPhoneNumber() != null) user.setPhoneNumber(userDTO.getPhoneNumber());
            
            // Handle image update if provided
            if (image != null && !image.isEmpty()) {
                user.setProfileImage(image.getBytes());
            }
            
            Users updatedUser = userProfileRepository.save(user);
            return convertToDTO(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile: " + e.getMessage(), e);
        }
    }

    @Override
    public UsersDTO updateProfileImage(String email, MultipartFile image) {
        try {
            Users user = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
            
            user.setProfileImage(image.getBytes());
            Users updatedUser = userProfileRepository.save(user);
            return convertToDTO(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile image", e);
        }
    }

    @Override
    public List<FoodDTO> getUserFavorites(String email) {
        return List.of(); 
    }

    @Override
    public void addFavorite(String email, Long foodId) {
        
    }

    @Override
    public void removeFavorite(String email, Long foodId) {
        
    }

    @Override
        public UsersDTO updatePreferences(String email, Map<String, Object> preferences) throws ResourceNotFoundException {
        return getUserProfile(email);
    }

   
    private UsersDTO convertToDTO(Users user) {
        UsersDTO dto = new UsersDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());
        
        if (user.getProfileImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getProfileImage());
            dto.setProfileImage("data:image/*;base64," + base64Image);
        }
        
        return dto;
    }

    @Override
    public void changePassword(String email, String currentPassword, String newPassword) 
            throws ResourceNotFoundException {
        Users user = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Validate new password
        if (newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters long");
        }

        // Encode and save new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userProfileRepository.save(user);
    }
}
