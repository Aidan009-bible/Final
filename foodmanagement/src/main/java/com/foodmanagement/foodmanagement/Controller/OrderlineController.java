package com.foodmanagement.foodmanagement.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodmanagement.foodmanagement.service.OrderlineService;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/orders")
public class OrderlineController {

    @Autowired
    private OrderlineService orderLineService;

    // @Autowired
    // private OrderlineRepository orderLineRepository;

    @GetMapping("/total-sales")
    public ResponseEntity<Map<String, Object>> getOrdersTotalSales() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalSales", orderLineService.getTotalSales());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/total_quantity")
    public ResponseEntity<Map<String, Object>> getFoodsTotalSales() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalQuantity", orderLineService.getTotalQuantity());
        return ResponseEntity.ok(response);
    }

}
