package com.foodmanagement.foodmanagement.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.stereotype.Service;

import com.foodmanagement.foodmanagement.dto.ToppingDTO;
import com.foodmanagement.foodmanagement.entity.Topping;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;
import com.foodmanagement.foodmanagement.repository.ToppingRepository;
import com.foodmanagement.foodmanagement.service.ToppingService;

@Service
public class ToppingServiceImpl implements ToppingService {

    private final ToppingRepository toppingRepository;

    public ToppingServiceImpl(ToppingRepository toppingRepository) {
        this.toppingRepository = toppingRepository;
    }

    @Override
    public List<ToppingDTO> getAllToppings(String sort) {
        List<Topping> toppings = toppingRepository.findAll();

        // Sorting logic
        if (sort != null) {
            switch (sort.toLowerCase()) {
                case "name":
                    toppings.sort(Comparator.comparing(Topping::getName));
                    break;
                case "date":
                    toppings.sort(Comparator.comparing(Topping::getModifiedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())));
                    break;
                default:
                    // Default sorting by modified date
                    toppings.sort(Comparator.comparing(Topping::getModifiedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())));
                    break;
            }
        }

        return toppings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ToppingDTO getToppingById(Integer id) throws ResourceNotFoundException {
        Topping topping = toppingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topping not found with id: " + id));
        return convertToDTO(topping);
    }

    @Override
    public ToppingDTO createTopping(ToppingDTO toppingDTO) {
        Topping topping = convertToEntity(toppingDTO);
        Topping savedTopping = toppingRepository.save(topping);
        return convertToDTO(savedTopping);
    }

    @Override
    public ToppingDTO updateTopping(Integer id, ToppingDTO toppingDTO) throws ResourceNotFoundException {
        Topping existingTopping = toppingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topping not found with id: " + id));
        existingTopping.setName(toppingDTO.getName());
        existingTopping.setPrice(toppingDTO.getPrice());
        existingTopping.setIsAvailable(toppingDTO.getIsAvailable());
        Topping updatedTopping = toppingRepository.save(existingTopping);
        return convertToDTO(updatedTopping);
    }

    @Override
    public void deleteTopping(Integer id) {
        toppingRepository.deleteById(id);
    }

    private ToppingDTO convertToDTO(Topping topping) {
        ToppingDTO dto = new ToppingDTO();
        dto.setId(topping.getId());
        dto.setName(topping.getName());
        dto.setPrice(topping.getPrice());
        dto.setIsAvailable(topping.getIsAvailable());
        dto.setModifiedDate(topping.getModifiedDate());
        dto.setCreatedDate(topping.getCreatedDate());
        
        // Get the count of foods associated with this topping
        Long foodCount = toppingRepository.countFoodsByToppingId(topping.getId());
        dto.setFoodCount(foodCount != null ? foodCount.intValue() : 0);
        
        return dto;
    }

    private Topping convertToEntity(ToppingDTO dto) {
        Topping topping = new Topping();
        topping.setName(dto.getName());
        topping.setPrice(dto.getPrice());
        topping.setIsAvailable(dto.getIsAvailable());
        return topping;
    }
}