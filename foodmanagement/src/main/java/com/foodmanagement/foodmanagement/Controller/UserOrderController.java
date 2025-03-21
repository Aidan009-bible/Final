package com.foodmanagement.foodmanagement.Controller;


import com.foodmanagement.foodmanagement.entity.Ordertable;
import com.foodmanagement.foodmanagement.entity.Orderline;
import com.foodmanagement.foodmanagement.service.OrdertableService;
import com.foodmanagement.foodmanagement.service.OrderlineService;
import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/users/orders")
@CrossOrigin(origins = "*")
public class UserOrderController {

    @Autowired
    private OrdertableService ordertableService;

    @Autowired
    private OrderlineService orderlineService;

    @GetMapping("/current")
    public ResponseEntity<List<Map<String, Object>>> getCurrentOrders() {
        // Get the authenticated user's email from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        
        // Get orders that are not in DELIVERED or CANCELLED status for the current user
        List<Ordertable> orders = ordertableService.getCurrentOrdersByUserEmail(email);
        List<Map<String, Object>> currentOrders = new ArrayList<>();

        for (Ordertable order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderId", order.getId());
            orderMap.put("orderCode", order.getOrderCode());
            orderMap.put("status", order.getStatus().toString());
            
            // Add null check for orderDate
            LocalDateTime orderDate = order.getOrderDate();
            if (orderDate != null) {
                orderMap.put("orderDate", orderDate.format(DateTimeFormatter.ISO_DATE));
            } else {
                orderMap.put("orderDate", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
            }
            
            orderMap.put("total", order.getTotalAmount());

            // Get order items
            List<Orderline> orderLines = orderlineService.findByOrderId(order.getId());
            List<Map<String, Object>> items = new ArrayList<>();
            
            for (Orderline line : orderLines) {
                Map<String, Object> item = new HashMap<>();
                item.put("foodTitle", line.getFoodTitle());
                item.put("quantity", line.getQuantity());
                item.put("price", line.getPrice());
                items.add(item);
            }
            orderMap.put("items", items);
            
            currentOrders.add(orderMap);
        }
        
        return ResponseEntity.ok(currentOrders);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getOrderHistory() {
        // Get the authenticated user's email from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        
        // Get completed or cancelled orders for the current user
        List<Ordertable> orders = ordertableService.getOrderHistoryByUserEmail(email);
        List<Map<String, Object>> orderHistory = new ArrayList<>();

        for (Ordertable order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderId", order.getId());
            orderMap.put("orderCode", order.getOrderCode());
            orderMap.put("status", order.getStatus().toString());
            
            // Add null check for orderDate
            LocalDateTime orderDate = order.getOrderDate();
            if (orderDate != null) {
                orderMap.put("orderDate", orderDate.format(DateTimeFormatter.ISO_DATE));
            } else {
                orderMap.put("orderDate", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
            }
            
            orderMap.put("total", order.getTotalAmount());

            // Get order items
            List<Orderline> orderLines = orderlineService.findByOrderId(order.getId());
            List<Map<String, Object>> items = new ArrayList<>();
            
            for (Orderline line : orderLines) {
                Map<String, Object> item = new HashMap<>();
                item.put("foodTitle", line.getFoodTitle());
                item.put("quantity", line.getQuantity());
                item.put("price", line.getPrice());
                items.add(item);
            }
            orderMap.put("items", items);
            
            orderHistory.add(orderMap);
        }
        
        // Sort by date descending (newest first)
        orderHistory.sort((o1, o2) -> {
            String date1 = (String) o1.get("orderDate");
            String date2 = (String) o2.get("orderDate");
            return date2.compareTo(date1);
        });
        
        return ResponseEntity.ok(orderHistory);
    }

    @PutMapping("/{orderId}/comment")
    public ResponseEntity<?> updateOrderComment(@PathVariable Integer orderId, @RequestBody Map<String, String> payload) {
        try {
            // Get the authenticated user's email
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = (String) authentication.getPrincipal();
            
            String comment = payload.get("comment");
            
            // Get the order
            Optional<Ordertable> orderOpt = ordertableService.getOrderById(orderId);
            if (!orderOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Ordertable order = orderOpt.get();
            
            // Verify that the order belongs to the authenticated user
            if (!order.getUser().getEmail().equals(userEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to modify this order");
            }
            
            order.setComment(comment);
            ordertableService.updateOrder(order);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to update comment: " + e.getMessage());
        }
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable Integer orderId) {
        // Get the authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        
        // Get the order details
        Ordertable order = ordertableService.getOrderByIdAndUserEmail(orderId, email);
        
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("orderId", order.getId());
        orderDetails.put("orderCode", order.getOrderCode());
        orderDetails.put("status", order.getStatus().toString());
        orderDetails.put("orderDate", order.getOrderDate().format(DateTimeFormatter.ISO_DATE));
        orderDetails.put("total", order.getTotalAmount());
        orderDetails.put("comment", order.getComment());
        orderDetails.put("paymentMethod", order.getPaymentMethod().toString());
        orderDetails.put("deliveryAddress", order.getDeliAddress());

        // Get order items
        List<Orderline> orderLines = orderlineService.findByOrderId(order.getId());
        List<Map<String, Object>> items = new ArrayList<>();
        
        for (Orderline line : orderLines) {
            Map<String, Object> item = new HashMap<>();
            item.put("foodTitle", line.getFoodTitle());
            item.put("quantity", line.getQuantity());
            item.put("price", line.getPrice());
            item.put("subtotal", line.getPrice() * line.getQuantity());
            items.add(item);
        }
        orderDetails.put("items", items);
        
        return ResponseEntity.ok(orderDetails);
    }
}