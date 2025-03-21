package com.foodmanagement.foodmanagement.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;

import com.foodmanagement.foodmanagement.service.FoodService;
import com.foodmanagement.foodmanagement.service.OrdertableService;
import com.foodmanagement.foodmanagement.service.UsersService;
import com.foodmanagement.foodmanagement.entity.Food;
import com.foodmanagement.foodmanagement.entity.Orderline;
import com.foodmanagement.foodmanagement.entity.Ordertable;
import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;
import com.foodmanagement.foodmanagement.entity.enums.PaymentMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/orders")
public class OrdertableController {

    @Autowired
    private OrdertableService ordertableService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private FoodService foodService;

    @GetMapping("/{orderId}/status")
    public String getOrderStatus(@PathVariable("orderId") int orderId) {
        // Retrieve the order status based on the orderId
        String status = ordertableService.getOrderStatus(orderId); // Example service

        return status;
    }

    @GetMapping("/{orderDate}/count")
    public ResponseEntity<Map<String, Integer>> getOrdersCountByDate(
            @PathVariable String orderDate,
            @RequestParam(required = false) String status) {

        System.out.println("Fetching orders for date: " + orderDate + ", status: " + status);
        Map<String, Integer> response = new HashMap<>();

        try {
            LocalDate date = LocalDate.parse(orderDate); // Parse the date string to LocalDate
            LocalDateTime startOfDay = date.atStartOfDay(); 
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            if (status != null) {
                try {
                    OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());

                    int count = ordertableService.getOrderCountByDateAndStatus(startOfDay, endOfDay, orderStatus);
                    response.put("count", count);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid status: " + status);
                    response.put("count", 0);
                    return ResponseEntity.ok(response);
                }
            } else {
                 
                int count = ordertableService.getOrderCountByDate(startOfDay, endOfDay);
                response.put("count", count);
            }
            return ResponseEntity.ok(response);

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + orderDate);
            response.put("count", 0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error getting order count: " + e.getMessage());
            response.put("count", 0);
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            System.out.println("Received update request - ID: " + id + ", Status: " + statusUpdate.get("status"));

            Integer idInt = Integer.parseInt(id);
            Optional<Ordertable> orderOpt = ordertableService.getOrderById(idInt);

            if (!orderOpt.isPresent()) {
                System.out.println("Order not found with ID: " + id);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Order not found");
                response.put("id", id);
                response.put("newStatus", statusUpdate.get("status"));
                return ResponseEntity.ok(response);
            }

            Ordertable order = orderOpt.get();
            OrderStatus newStatus = OrderStatus.valueOf(statusUpdate.get("status").toUpperCase());
            order.setStatus(newStatus);

            // Explicitly save the order and ensure it's saved
            Ordertable savedOrder = ordertableService.saveOrder(order);
            System.out.println("Order updated successfully - ID: " + id + ", New Status: " + newStatus);

            if (savedOrder == null) {
                throw new RuntimeException("Failed to save order");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order status updated LEE");
            response.put("id", id);
            response.put("newStatus", statusUpdate.get("status"));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error updating order - ID: " + id + ", Error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update order status LEE: " + e.getMessage());
            response.put("id", id);
            response.put("newStatus", statusUpdate.get("status"));
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{year}/{month}/total-sales")
    public ResponseEntity<Map<String, Object>> getTotalSalesByMonth(
            @PathVariable int year,
            @PathVariable int month) {

        Map<String, Object> response = new HashMap<>();

        try {
            double totalSales = ordertableService.getTotalSalesByMonth(year, month);
            response.put("totalSales", totalSales);
            response.put("year", year);
            response.put("month", month);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error getting total sales: " + e.getMessage());
            response.put("error", "Failed to get total sales");
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{year}/total-sales")
    public ResponseEntity<Map<String, Object>> getTotalSalesByYear(
            @PathVariable int year) {

        Map<String, Object> response = new HashMap<>();

        try {
            double totalSales = ordertableService.getTotalSalesByYear(year);
            response.put("totalSales", totalSales);
            response.put("year", year);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error getting total sales: " + e.getMessage());
            response.put("error", "Failed to get total sales");
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{year}/{month}/count")
    public ResponseEntity<Map<String, Object>> getOrderCountByMonth(
            @PathVariable int year,
            @PathVariable int month) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Convert year and month to start and end dates
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            int orderCount = ordertableService.getOrderCountByDate(startDateTime, endDateTime);
            response.put("count", orderCount);
            response.put("year", year);
            response.put("month", month);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error getting order count: " + e.getMessage());
            response.put("error", "Failed to get order count");
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/year/{year}/count")
    public ResponseEntity<Map<String, Object>> getOrderCountByYear(
            @PathVariable int year) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Convert year to start and end dates
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            int orderCount = ordertableService.getOrderCountByDate(startDateTime, endDateTime);
            response.put("count", orderCount);
            response.put("year", year);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error getting order count: " + e.getMessage());
            response.put("error", "Failed to get order count");
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/daily/{date}/total-sales")
    public ResponseEntity<Map<String, Object>> getTotalSalesByDate(
            @PathVariable String date) {

        Map<String, Object> response = new HashMap<>();

        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startDateTime = localDate.atStartOfDay();
            LocalDateTime endDateTime = localDate.atTime(LocalTime.MAX);

            double totalSales = ordertableService.getTotalSalesByDate(startDateTime, endDateTime);
            response.put("totalSales", totalSales);
            response.put("date", date);
            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + date);
            response.put("error", "Failed to get total sales");
            response.put("message", "Invalid date format. Use YYYY-MM-DD");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error getting total sales: " + e.getMessage());
            response.put("error", "Failed to get total sales");
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getOrdersCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("count", ordertableService.getTotalOrderCount());
        return ResponseEntity.ok(response);
    }

    // @GetMapping("/total-sales")
    // public ResponseEntity<Map<String, Object>> getOrdersTotalSales() {
    // Map<String, Object> response = new HashMap<>();
    // response.put("totalSales", ordertableService.getTotalSales());
    // return ResponseEntity.ok(response);
    // }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> orderData) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract data from request
            Integer userId = (Integer) orderData.get("user_id");
            String address = (String) orderData.get("address");
            Double total = Double.valueOf(orderData.get("total").toString());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");

            // Create new order
            Ordertable order = new Ordertable();
            order.setUser(usersService.getUserById(userId).orElseThrow());
            order.setOrderCode(generateOrderCode());
            order.setStatus(OrderStatus.PENDING);
            order.setOrderDate(LocalDateTime.now());
            order.setTotalAmount(total);
            order.setPaymentMethod(PaymentMethod.CASH);
            order.setDeliAddress(address);

            // Create orderlines
            List<Orderline> orderLines = new ArrayList<>();
            for (Map<String, Object> item : items) {
                Orderline orderline = new Orderline();
                orderline.setOrder(order);

                Integer foodId = (Integer) item.get("foodId");
                Food food = foodService.geTFoodById(foodId).orElseThrow();
                orderline.setFood(food);

                orderline.setQuantity(Long.valueOf(item.get("quantity").toString()));
                orderline.setPrice(Double.valueOf(item.get("price").toString()));
                orderline.setTotalAmount(Double.valueOf(item.get("totalAmount").toString()));
                orderline.setFoodTitle(food.getTitle());

                orderLines.add(orderline);
            }

            order.setOrderLines(orderLines);

            // Save order
            Ordertable savedOrder = ordertableService.createOrder(order);

            response.put("success", true);
            response.put("message", "Order created successfully");
            response.put("orderId", savedOrder.getId());
            response.put("orderCode", savedOrder.getOrderCode());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create order: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String generateOrderCode() {
        // Implementation to generate unique order code
        return "ORD-" + System.currentTimeMillis();
    }

}
