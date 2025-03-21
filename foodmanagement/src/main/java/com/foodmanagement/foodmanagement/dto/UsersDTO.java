package com.foodmanagement.foodmanagement.dto;

import com.foodmanagement.foodmanagement.entity.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersDTO {
    private Integer id;
    private Role role;
    private String name;
    private String email;
    private String password;
    private String token;
    private String phoneNumber;
    private String address;
    private boolean isEnabled;
    private boolean accountNonLocked;
    private String profileImage; // Store Base64 string or null
}
