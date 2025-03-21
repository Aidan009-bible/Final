package com.foodmanagement.foodmanagement.Controller;

import com.foodmanagement.foodmanagement.entity.Ordersummary;

import com.foodmanagement.foodmanagement.service.OrdersummaryService;
import com.foodmanagement.foodmanagement.dto.OrdersummaryDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrdersummaryController {

    @Autowired
    private OrdersummaryService ordersummaryService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrderSummaries(
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String sort) {

        List<Ordersummary> orderSummaries;

        // Handle date range filtering
        if (startDate != null && endDate != null) {
            try {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                LocalDateTime startDateTime = start.atStartOfDay();
                LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

                orderSummaries = ordersummaryService.getOrderSummariesByDateRange(startDateTime, endDateTime);
            } catch (Exception e) {
                System.err.println("Error parsing dates: " + e.getMessage());
                orderSummaries = ordersummaryService.getAllOrderSummaries();
            }
        } else {
            orderSummaries = ordersummaryService.getAllOrderSummaries();
        }

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> formattedOrders = new ArrayList<>();

        // Format orders
        for (Ordersummary order : orderSummaries) {
            Map<String, Object> orderMap = new HashMap<>();

            // Use orderCode instead of orderId
            orderMap.put("code", order.getOrderCode());
            orderMap.put("orderId", order.getOrderId());

            // Customer details
            Map<String, String> customer = new HashMap<>();
            customer.put("name", order.getName());
            customer.put("phone", order.getPhoneNumber() != null ? order.getPhoneNumber() : "");
            customer.put("address", order.getAddress() != null ? order.getAddress() : "");
            orderMap.put("customer", customer);

            // Order date and time
            String[] dateTime = order.getOrderDate().toString().split("T");
            orderMap.put("date", dateTime[0]);
            orderMap.put("time", dateTime[1].substring(0, 5));

            // Order items
            List<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("title", order.getFoodTitle());
            item.put("quantity", order.getQuantity());
            item.put("price", order.getPrice());
            items.add(item);
            orderMap.put("items", items);

            // Other order details
            orderMap.put("total", order.getTotalAmount());
            orderMap.put("status", order.getStatus().toString().toLowerCase());
            orderMap.put("paymentMethod", order.getPaymentMethod().toString());

            formattedOrders.add(orderMap);
        }

        // Apply payment method filter
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            formattedOrders = formattedOrders.stream()
                    .filter(order -> order.get("paymentMethod").toString().equalsIgnoreCase(paymentMethod))
                    .collect(Collectors.toList());
        }

        // Apply status filter
        if (orderStatus != null && !orderStatus.isEmpty()) {
            formattedOrders = formattedOrders.stream()
                    .filter(order -> order.get("status").toString().equalsIgnoreCase(orderStatus))
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if (sort != null && !sort.isEmpty()) {
            switch (sort.toLowerCase()) {
                case "oid":
                    formattedOrders.sort((o1, o2) -> {
                        String code1 = ((String) o1.get("code")).replace("#", "");
                        String code2 = ((String) o2.get("code")).replace("#", "");
                        return code1.compareTo(code2);
                    });
                    break;
                case "date":
                    formattedOrders.sort((o1, o2) -> {
                        String date1 = (String) o1.get("date");
                        String time1 = (String) o1.get("time");
                        String date2 = (String) o2.get("date");
                        String time2 = (String) o2.get("time");
                        int dateCompare = date2.compareTo(date1);
                        return dateCompare != 0 ? dateCompare : time2.compareTo(time1);
                    });
                    break;
                case "customername":
                    formattedOrders.sort((o1, o2) -> {
                        @SuppressWarnings("unchecked")
                        Map<String, String> customer1 = (Map<String, String>) o1.get("customer");
                        @SuppressWarnings("unchecked")
                        Map<String, String> customer2 = (Map<String, String>) o2.get("customer");
                        return customer1.get("name").compareToIgnoreCase(customer2.get("name"));
                    });
                    break;
                case "total":
                    formattedOrders.sort((o1, o2) -> {
                        Double total1 = (Double) o1.get("total");
                        Double total2 = (Double) o2.get("total");
                        return total2.compareTo(total1); // Sort in descending order
                    });
                    break;
                case "status":
                    formattedOrders.sort((o1, o2) -> {
                        String status1 = (String) o1.get("status");
                        String status2 = (String) o2.get("status");
                        return status1.compareToIgnoreCase(status2);
                    });
                    break;
            }
        }

        response.put("orders", formattedOrders);
        return ResponseEntity.ok(response);
    }

    // Get order summary by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Ordersummary> getOrderSummaryById(@PathVariable Integer orderId) {
        Optional<Ordersummary> orderSummary = ordersummaryService.getOrderSummaryById(orderId);
        return orderSummary.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get order summaries by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrdersummaryDTO>> getOrderSummariesByStatus(@PathVariable String status) {
        List<OrdersummaryDTO> orderSummaries = ordersummaryService.getOrderSummariesByStatus(status);
        return ResponseEntity.ok(orderSummaries);
    }

    // Get order summaries by payment method
    @GetMapping("/payment/{paymentMethod}")
    public ResponseEntity<List<OrdersummaryDTO>> getOrderSummariesByPaymentMethod(@PathVariable String paymentMethod) {
        List<OrdersummaryDTO> orderSummaries = ordersummaryService.getOrderSummariesByPaymentMethod(paymentMethod);
        return ResponseEntity.ok(orderSummaries);
    }

}