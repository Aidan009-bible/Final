package com.foodmanagement.foodmanagement.service;


import java.util.List;

import com.foodmanagement.foodmanagement.dto.ToppingDTO;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;

public interface ToppingService {
    List<ToppingDTO> getAllToppings(String sort);
    
    ToppingDTO getToppingById(Integer id) throws ResourceNotFoundException;
    
    ToppingDTO createTopping(ToppingDTO toppingDTO) throws ResourceNotFoundException;
    
    ToppingDTO updateTopping(Integer id, ToppingDTO toppingDTO) throws ResourceNotFoundException;
    
    void deleteTopping(Integer id) throws ResourceNotFoundException;
}