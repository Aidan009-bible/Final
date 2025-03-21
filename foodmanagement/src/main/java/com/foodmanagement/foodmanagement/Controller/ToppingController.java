package com.foodmanagement.foodmanagement.Controller;

//import java.time.LocalDate;
//import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.foodmanagement.foodmanagement.dto.ToppingDTO;
import com.foodmanagement.foodmanagement.exception.ResourceNotFoundException;
import com.foodmanagement.foodmanagement.service.ToppingService;

@RestController
@RequestMapping("/api/toppings")
@CrossOrigin(origins = "*")
public class ToppingController {

    private final ToppingService toppingService;

    public ToppingController(ToppingService toppingService) {
        this.toppingService = toppingService;
    }

    // GET: Retrieve all toppings from DB
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getToppings(@RequestParam(required = false) String sort) {
        List<ToppingDTO> toppings = toppingService.getAllToppings(sort);
        List<Map<String, Object>> response = toppings.stream().map(topping -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", topping.getId());
            map.put("name", topping.getName());
            map.put("price", topping.getPrice());
            map.put("isAvailable", topping.getIsAvailable());
            map.put("dateModified", topping.getModifiedDate().toLocalDate().toString());
            map.put("time", topping.getModifiedDate().format(DateTimeFormatter.ofPattern("hh:mm a")));
            map.put("itemCount", topping.getFoodCount());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET: Retrieve a single topping by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getToppingById(@PathVariable Integer id)
            throws ResourceNotFoundException {
        ToppingDTO topping = toppingService.getToppingById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("id", topping.getId());
        response.put("name", topping.getName());
        response.put("price", topping.getPrice());
        response.put("isAvailable", topping.getIsAvailable());
        response.put("dateModified", topping.getModifiedDate().toLocalDate().toString());
        response.put("time", topping.getModifiedDate().format(DateTimeFormatter.ofPattern("hh:mm a")));
        response.put("itemCount", topping.getFoodCount());
        return ResponseEntity.ok(response);
    }

    // POST: Create a new topping
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addTopping(
            @RequestPart String name,
            @RequestPart String price,
            @RequestPart(required = false) String isAvailable) {
        try {
            ToppingDTO dto = new ToppingDTO();
            dto.setName(name);
            dto.setPrice(Double.parseDouble(price));
            dto.setIsAvailable(isAvailable != null ? Boolean.parseBoolean(isAvailable) : true);

            ToppingDTO savedTopping = toppingService.createTopping(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedTopping.getId());
            response.put("name", savedTopping.getName());
            response.put("price", savedTopping.getPrice());
            response.put("isAvailable", savedTopping.getIsAvailable());
            response.put("dateModified", savedTopping.getModifiedDate().toLocalDate().toString());
            response.put("time", savedTopping.getModifiedDate().format(DateTimeFormatter.ofPattern("hh:mm a")));
            response.put("itemCount", savedTopping.getFoodCount());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT: Update an existing topping
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateTopping(
            @PathVariable Integer id,
            @RequestPart String name,
            @RequestPart String price,
            @RequestPart(required = false) String isAvailable) {
        try {
            ToppingDTO dto = new ToppingDTO();
            dto.setName(name);
            dto.setPrice(Double.parseDouble(price));
            dto.setIsAvailable(isAvailable != null ? Boolean.parseBoolean(isAvailable) : true);

            ToppingDTO updatedTopping = toppingService.updateTopping(id, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedTopping.getId());
            response.put("name", updatedTopping.getName());
            response.put("price", updatedTopping.getPrice());
            response.put("isAvailable", updatedTopping.getIsAvailable());
            response.put("dateModified", updatedTopping.getModifiedDate().toLocalDate().toString());
            response.put("time", updatedTopping.getModifiedDate().format(DateTimeFormatter.ofPattern("hh:mm a")));
            response.put("itemCount", updatedTopping.getFoodCount());

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE: Remove a topping from the DB
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTopping(@PathVariable Integer id)
            throws ResourceNotFoundException {
        toppingService.deleteTopping(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Topping deleted successfully");
        return ResponseEntity.ok(response);
    }
}