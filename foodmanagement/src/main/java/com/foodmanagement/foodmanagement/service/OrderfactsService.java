package com.foodmanagement.foodmanagement.service;

import com.foodmanagement.foodmanagement.entity.Orderfacts;
import com.foodmanagement.foodmanagement.repository.OrderfactsRepository;
import com.foodmanagement.foodmanagement.dto.OrderfactsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderfactsService {

    @Autowired
    private OrderfactsRepository orderfactsRepository;

    // Convert Entity to DTO
    private OrderfactsDTO convertToDTO(Orderfacts orderfacts) {
        return new OrderfactsDTO(
            orderfacts.getTotalOrders(),
            orderfacts.getTotalQuantity(),
            orderfacts.getTotalSales(),
            orderfacts.getTotalUsers()
        );
    }

    // Get all order facts
    public List<Orderfacts> getAllOrderFacts() {
        List<Orderfacts> facts = orderfactsRepository.findAll();
        System.out.println("Found " + facts.size() + " order facts records");
        return facts;
    }

    // Get latest order facts
    public OrderfactsDTO getLatestOrderFacts() {
        List<Orderfacts> facts = orderfactsRepository.findAll();
        if (!facts.isEmpty()) {
            // Get the latest record
            return convertToDTO(facts.get(facts.size() - 1));
        }
        return null;
    }
} 