package com.foodmanagement.foodmanagement.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.foodmanagement.foodmanagement.dto.UsersDTO;
import com.foodmanagement.foodmanagement.dto.FoodDTO;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;
import com.foodmanagement.foodmanagement.service.UserProfileService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // The principal is now just the email string
        String email = (String) authentication.getPrincipal();
        System.out.println("Authenticated email: " + email);  // Debugging line
        return email;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        try {
            String userEmail = getCurrentUserEmail();
            UsersDTO profile = userProfileService.getUserProfile(userEmail);

            Map<String, Object> response = new HashMap<>();
            response.put("id", profile.getId());
            response.put("name", profile.getName());
            response.put("email", profile.getEmail());
            response.put("phone", profile.getPhoneNumber());
            response.put("address", profile.getAddress());
            response.put("image", profile.getProfileImage());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) MultipartFile image) {
        try {
            String currentEmail = getCurrentUserEmail();
            // Validate input
            if (name != null && name.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Name cannot be empty"));
            }
            if (phone != null && !phone.matches("\\d{10}")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid phone number format"));
            }
            
            // Create DTO with validated data
            UsersDTO userDTO = new UsersDTO();
            userDTO.setName(name);
            userDTO.setEmail(currentEmail);
            userDTO.setPhoneNumber(phone);
            userDTO.setAddress(address);
            
            // Validate image if provided
            if (image != null && !image.isEmpty()) {
                if (!image.getContentType().startsWith("image/")) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid file type. Only images are allowed"));
                }
                if (image.getSize() > 5242880) { // 5MB limit
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Image size should not exceed 5MB"));
                }
            }
            
            UsersDTO updatedDto = userProfileService.updateUserProfile(currentEmail, userDTO, image);
            
            // Prepare response with updated data
            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedDto.getId());
            response.put("name", updatedDto.getName());
            response.put("email", updatedDto.getEmail());
            response.put("phone", updatedDto.getPhoneNumber());
            response.put("address", updatedDto.getAddress());
            response.put("image", updatedDto.getProfileImage());
            response.put("dateModified", LocalDate.now().toString());
            response.put("time", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
            response.put("message", "Profile updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update profile: " + e.getMessage()));
        }
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Map<String, Object>>> getFavorites() throws ResourceNotFoundException {
        String email = getCurrentUserEmail();
        List<FoodDTO> favorites = userProfileService.getUserFavorites(email);
        List<Map<String, Object>> response = new ArrayList<>();
        for (FoodDTO food : favorites) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", food.getId());
            map.put("name", food.getName());
            map.put("description", food.getDescription());
            map.put("image", food.getImage());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/favorites/{foodId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long foodId) throws ResourceNotFoundException {
        String email = getCurrentUserEmail();
        userProfileService.addFavorite(email, foodId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorites/{foodId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long foodId) throws ResourceNotFoundException {
        String email = getCurrentUserEmail();
        userProfileService.removeFavorite(email, foodId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        try {
            String email = getCurrentUserEmail();
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Current password and new password are required"));
            }

            userProfileService.changePassword(email, currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("UserProfileController is working");
    }
}
