package com.foodmanagement.foodmanagement.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodmanagement.foodmanagement.service.UsersService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UsersController {
    @Autowired
    private UsersService usersService;
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUsersCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("count", usersService.getUsersCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{date}/count")
    public ResponseEntity<Map<String, Object>> getUsersCountByDate(@PathVariable String date) {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime startOfDay = LocalDate.parse(date).atStartOfDay();
            LocalDateTime endOfDay = LocalDate.parse(date).atTime(LocalTime.MAX);
            response.put("count", usersService.getUsersCountByDate(startOfDay, endOfDay));
            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            response.put("error", "Invalid date format");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
