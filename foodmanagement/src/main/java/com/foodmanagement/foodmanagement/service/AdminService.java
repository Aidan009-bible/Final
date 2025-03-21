package com.foodmanagement.foodmanagement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodmanagement.foodmanagement.dto.UsersDTO;
import com.foodmanagement.foodmanagement.entity.Users;
import com.foodmanagement.foodmanagement.entity.enums.Role;
import com.foodmanagement.foodmanagement.repository.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public List<UsersDTO> getAllUsers() {
        List<Users> users = adminRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UsersDTO updateUserRole(int userId, Role newRole) {
        Users user = adminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        Users updatedUser = adminRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public long getUserCount() {
        return adminRepository.count();
    }

    private UsersDTO convertToDTO(Users user) {
        return UsersDTO.builder()
                .id(user.getId())
                .role(user.getRole())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .isEnabled(user.getIsEnabled())
                .accountNonLocked(user.getAccountNonLocked())
                .profileImage(user.getProfileImage() != null ? 
                    "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(user.getProfileImage()) : 
                    null)
                .build();
    }
}