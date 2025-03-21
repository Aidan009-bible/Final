package com.foodmanagement.foodmanagement.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodmanagement.foodmanagement.dto.OrderfactsDTO;
import com.foodmanagement.foodmanagement.entity.Orderfacts;
//import com.restaurant.model.enums.OrderStatus;
import com.foodmanagement.foodmanagement.service.OrderfactsService;

@RestController
@RequestMapping("/api/orderfacts")
@CrossOrigin(origins = "*")
public class OrderfactsController {

    @Autowired
    private OrderfactsService orderfactsService;

    // Get all order facts
    @GetMapping("/facts")
    public ResponseEntity<Map<String, Object>> getAllOrderFacts() {
        List<Orderfacts> orderFacts = orderfactsService.getAllOrderFacts();

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> formattedFacts = new ArrayList<>();

        for (Orderfacts fact : orderFacts) {
            Map<String, Object> factMap = new HashMap<>();
            factMap.put("totalOrders", fact.getTotalOrders());
            factMap.put("totalQuantity", fact.getTotalQuantity());
            factMap.put("totalSales", fact.getTotalSales());
            factMap.put("totalUsers", fact.getTotalUsers());
            formattedFacts.add(factMap);
        }

        response.put("orderFacts", formattedFacts);
        return ResponseEntity.ok(response);
    }

    // Get latest order facts
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestOrderFacts() {
        try {
            OrderfactsDTO facts = orderfactsService.getLatestOrderFacts();
            if (facts != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("totalOrders", facts.getTotalOrders());
                response.put("totalQuantity", facts.getTotalQuantity());
                response.put("totalSales", facts.getTotalSales());
                response.put("totalUsers", facts.getTotalUsers());
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // Get dashboard stats (formatted for frontend)
    @GetMapping("/dashboard")
    public ResponseEntity<List<Map<String, Object>>> getDashboardStats() {
        try {
            OrderfactsDTO facts = orderfactsService.getLatestOrderFacts();
            if (facts != null) {
                List<Map<String, Object>> stats = new ArrayList<>();
                
                // Total Sales
                Map<String, Object> sales = new HashMap<>();
                sales.put("title", "Total Sales");
                sales.put("value", facts.getTotalSales());
                sales.put("formattedValue", String.format("$%.2f", facts.getTotalSales()));
                stats.add(sales);

                // Total Orders
                Map<String, Object> orders = new HashMap<>();
                orders.put("title", "Total Orders");
                orders.put("value", facts.getTotalOrders());
                stats.add(orders);

                // Total Users
                Map<String, Object> users = new HashMap<>();
                users.put("title", "Total Users");
                users.put("value", facts.getTotalUsers());
                stats.add(users);

                // Total Quantity
                Map<String, Object> quantity = new HashMap<>();
                quantity.put("title", "Items Sold");
                quantity.put("value", facts.getTotalQuantity());
                stats.add(quantity);

                return ResponseEntity.ok(stats);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.emptyList());
        }
    }

    // Get total sales
    @GetMapping("/total-sales")
    public ResponseEntity<Map<String, Object>> getTotalSales() {
        OrderfactsDTO facts = orderfactsService.getLatestOrderFacts();

        if (facts != null && facts.getTotalSales() != null) {
            Map<String, Object> response = new HashMap<>();

            response.put("totalSales", facts.getTotalSales());
            response.put("formattedSales", String.format("$%.2f", facts.getTotalSales()));
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }

    // @GetMapping("/count")
    // public ResponseEntity<Map<String, Object>> getOrdersCount() {
    /// Map<String, Object> response = new HashMap<>();
    // response.put("count", 23356);
    // return ResponseEntity.ok(response);
    // }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getOrdersCount() {
        OrderfactsDTO facts = orderfactsService.getLatestOrderFacts();

        Map<String, Object> response = new HashMap<>();
        if (facts != null && facts.getTotalOrders() != null) {
            response.put("count", facts.getTotalOrders());
        } else {
            response.put("count", 0); // Default value if no data found
        }
        return ResponseEntity.ok(response);
    }
    /*
     * @GetMapping("/{date}/count")
     * public ResponseEntity<Map<String, Integer>> getOrdersCountByDate(
     * 
     * @PathVariable String date,
     * 
     * @RequestParam(required = false) String status) {
     * 
     * System.out.println(date + ": " + status);
     * 
     * int sampleCount = 200;
     * Map<String, Integer> response = new HashMap<>();
     * 
     * if (status != null) {
     * try {
     * OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
     * 
     * switch (orderStatus) {
     * case PENDING:
     * response.put("count", sampleCount - 50);
     * break;
     * case DELIVERED:
     * response.put("count", sampleCount - 70);
     * break;
     * default:
     * response.put("count", sampleCount);
     * break;
     * }
     * } catch (IllegalArgumentException e) {
     * response.put("error", -1); // Indicate an invalid status
     * return ResponseEntity.badRequest().body(response);
     * }
     * } else {
     * // No status provided, return total count
     * response.put("count", sampleCount);
     * }
     * return ResponseEntity.ok(response);
     * }
     */

}