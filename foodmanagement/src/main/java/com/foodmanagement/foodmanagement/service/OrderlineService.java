package com.foodmanagement.foodmanagement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodmanagement.foodmanagement.dto.OrderlineDTO;
import com.foodmanagement.foodmanagement.entity.Orderline;
import com.foodmanagement.foodmanagement.entity.OrderlineId;
import com.foodmanagement.foodmanagement.repository.OrderlineRepository;
import com.foodmanagement.foodmanagement.entity.enums.OrderStatus;

@Service
public class OrderlineService {

    @Autowired
    private OrderlineRepository orderlineRepository;

    public List<Orderline> getAllOrders() {
        return orderlineRepository.findAll();
    }

    public double calculateTotalSales() {
        return orderlineRepository.findAll()
                .stream()
                .filter(orderline -> orderline.getOrder().getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(orderline -> orderline.getPrice() * orderline.getQuantity())
                .sum();
    }

    // Convert Entity to DTO
    private OrderlineDTO convertToDTO(Orderline orderline) {
        return new OrderlineDTO(
                orderline.getOrder().getId(),
                orderline.getFood().getId(),
                orderline.getQuantity(),
                orderline.getPrice(),
                orderline.getFoodTitle(),
                orderline.getTotalAmount());
    }

    // Find by composite key
    public OrderlineDTO findById(OrderlineId id) {
        return orderlineRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // Find by orderId
    public List<Orderline> findByOrderId(Integer orderId) {
        return orderlineRepository.findByOrder_Id(orderId);
    }

    // Find by foodId
    public List<OrderlineDTO> findByFoodId(Integer foodId) {
        return orderlineRepository.findByFood_Id(foodId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public double getTotalSales() {
        return orderlineRepository.findAll().stream()
                .filter(orderline -> orderline.getOrder().getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(order -> order.getTotalAmount())
                .sum();
    }

    public Long getTotalQuantity() {
        return orderlineRepository.findAll().stream()
                .filter(orderline -> orderline.getOrder().getStatus() != OrderStatus.CANCELLED)
                .mapToLong(order -> order.getQuantity())
                .sum();

    }

    /*
     * public Optional<Orderline> getOrderById(int id) {
     * return orderLineRepository.findById(id);
     * }
     * 
     * public Orderline createOrder(Orderline order) {
     * // Calculate totalPrice manually in service if needed
     * 
     * return orderLineRepository.save(order); // Save the order to the database
     * }
     * 
     * public List<Orderline> updateOrders(int userID, Orderline updatedOrder) {
     * List<Orderline> existingOrders = orderLineRepository.findByUserID(userID);
     * 
     * if (!existingOrders.isEmpty()) {
     * for (Orderline order : existingOrders) {
     * order.setItem(updatedOrder.getItem());
     * order.setStatus(updatedOrder.getStatus());
     * order.setQuantity(updatedOrder.getQuantity());
     * order.setPrice(updatedOrder.getPrice());
     * 
     * // Recalculate totalPrice after updating the price and quantity
     * double totalPrice = order.getPrice() * order.getQuantity();
     * order.setTotalPrice(totalPrice);
     * }
     * 
     * return orderRepository.saveAll(existingOrders); // Save all updated orders
     * }
     * return Collections.emptyList(); // Return an empty list if no orders are
     * found
     * }
     * 
     * public List<Orderline> getOrdersByUserId(int userID) {
     * return orderLineRepository.findByUserID(userID); // Adjust according to your
     * repository
     * }
     * 
     * public List<Orderline> getOrdersByStatus(String status) {
     * return orderLineRepository.findByStatus(status);
     * }
     * 
     * public String deleteOrder(int id) {
     * if (orderLineRepository.existsById(id)) {
     * orderLineRepository.deleteById(id); // ✅ Actually deletes the order from the
     * database
     * return "Order deleted!";
     * } else {
     * return "Order not found!";
     * }
     * }
     * 
     * @PersistenceContext
     * private EntityManager entityManager;
     * 
     * public boolean existsByUserId(int userId) {
     * String query = "SELECT COUNT(o) FROM Orderline o WHERE o.userID = :userId";
     * Long count = (Long) entityManager.createQuery(query)
     * .setParameter("userId", userId)
     * .getSingleResult();
     * return count > 0;
     * }
     * 
     * @Transactional
     * public String deleteOrdersByUserId(int userId) {
     * int deletedCount =
     * entityManager.createQuery("DELETE FROM Orderline o WHERE o.userID = :userId")
     * .setParameter("userId", userId)
     * .executeUpdate();
     * 
     * if (deletedCount > 0) {
     * return "Deleted " + deletedCount + " orders for userId: " + userId;
     * } else {
     * return "No orders found for userId: " + userId;
     * }
     * }
     * 
     * public int getOrderCountByDate(String date) {
     * LocalDate localDate = LocalDate.parse(date);
     * 
     * // Convert LocalDate to LocalDateTime range for filtering by the full day
     * LocalDateTime startOfDay = localDate.atStartOfDay();
     * LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
     * 
     * // Use JPQL query instead of native SQL
     * TypedQuery<Long> query = entityManager.createQuery(
     * "SELECT COUNT(o) FROM Orderline o WHERE o.orderDate BETWEEN :start AND :end",
     * Long.class);
     * query.setParameter("start", startOfDay);
     * query.setParameter("end", endOfDay);
     * 
     * Long count = query.getSingleResult();
     * return count.intValue();
     * }
     * 
     * public String getMostOrderedItemByDate(String date) {
     * LocalDate localDate = LocalDate.parse(date);
     * 
     * // Convert LocalDate to LocalDateTime range for filtering
     * LocalDateTime startOfDay = localDate.atStartOfDay();
     * LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
     * 
     * // Query to find the most-ordered item
     * TypedQuery<Object[]> query = entityManager.createQuery(
     * "SELECT o.item, SUM(o.quantity) FROM Orderline o " +
     * "WHERE o.orderDate BETWEEN :start AND :end " +
     * "GROUP BY o.item ORDER BY SUM(o.quantity) DESC",
     * Object[].class);
     * 
     * query.setParameter("start", startOfDay);
     * query.setParameter("end", endOfDay);
     * query.setMaxResults(1); // Get only the top item
     * 
     * List<Object[]> result = query.getResultList();
     * 
     * if (result.isEmpty()) {
     * return "No orders found on this date.";
     * }
     * 
     * return (String) result.get(0)[0]; // Most-ordered item name
     * }
     * 
     * public String getMostPopularItemByDate(String date) {
     * LocalDate localDate = LocalDate.parse(date);
     * 
     * // Convert LocalDate to LocalDateTime range
     * LocalDateTime startOfDay = localDate.atStartOfDay();
     * LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
     * 
     * // Query to find the item ordered the most times (most unique orders)
     * TypedQuery<Object[]> query = entityManager.createQuery(
     * "SELECT o.item, COUNT(o.id) FROM Orderline o " +
     * "WHERE o.orderDate BETWEEN :start AND :end " +
     * "GROUP BY o.item ORDER BY COUNT(o.id) DESC",
     * Object[].class);
     * 
     * query.setParameter("start", startOfDay);
     * query.setParameter("end", endOfDay);
     * query.setMaxResults(1); // Get only the most popular item
     * 
     * List<Object[]> result = query.getResultList();
     * 
     * if (result.isEmpty()) {
     * return "No popular item found on this date.";
     * }
     * 
     * return (String) result.get(0)[0]; // Most popular item name
     * }
     * 
     * public List<Orderline> getOrdersByDate(String date) {
     * LocalDate localDate = LocalDate.parse(date);
     * 
     * // Convert LocalDate to LocalDateTime range for filtering orders of the full
     * day
     * LocalDateTime startOfDay = localDate.atStartOfDay();
     * LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
     * 
     * TypedQuery<Orderline> query = entityManager.createQuery(
     * "SELECT o FROM Orderline o WHERE o.orderDate BETWEEN :start AND :end",
     * Orderline.class);
     * 
     * query.setParameter("start", startOfDay);
     * query.setParameter("end", endOfDay);
     * 
     * return query.getResultList();
     * }
     * 
     * public Orderline updateOrder(int id, Orderline updatedOrder) {
     * Orderline existingOrder = orderLineRepository.findById(id).orElse(null);
     * 
     * if (existingOrder == null) {
     * return null; // If the order does not exist, return null.
     * }
     * 
     * // Update the fields of the existing order with the new data from the
     * // updatedOrder
     * existingOrder.setItem(updatedOrder.getItem());
     * existingOrder.setStatus(updatedOrder.getStatus());
     * existingOrder.setQuantity(updatedOrder.getQuantity());
     * existingOrder.setUserID(updatedOrder.getUserID());
     * existingOrder.setPrice(updatedOrder.getPrice()); // Ensure price is updated
     * 
     * double totalPrice = updatedOrder.getPrice() * updatedOrder.getQuantity();
     * updatedOrder.setTotalPrice(totalPrice);
     * // Order date remains the same unless you want to change it, in which case
     * you
     * // can manually set it:
     * // existingOrder.setOrderDate(updatedOrder.getOrderDate() != null ?
     * // updatedOrder.getOrderDate() : existingOrder.getOrderDate());
     * 
     * // Save the updated order to the database
     * return orderLineRepository.save(existingOrder);
     * }
     */
}
