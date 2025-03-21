package com.foodmanagement.foodmanagement.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.foodmanagement.foodmanagement.dto.UsersDTO;
import com.foodmanagement.foodmanagement.entity.enums.Role;
import com.foodmanagement.foodmanagement.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UsersDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UsersDTO> updateUserRole(
            @PathVariable int userId,
            @RequestBody Map<String, String> payload) {
        Role newRole = Role.valueOf(payload.get("role"));
        return ResponseEntity.ok(adminService.updateUserRole(userId, newRole));
    }

    @GetMapping("/users/count")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(adminService.getUserCount());
    }
}