package com.assignment.product_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.product_service.service.ItemService;
import com.assignment.product_service.vo.ItemVO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemVO> createItem(@Valid @RequestBody ItemVO itemVO) throws InterruptedException {
        return ResponseEntity.ok(itemService.createItem(itemVO));
    }

    @GetMapping
    public ResponseEntity<List<ItemVO>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }
    
    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        try {
            itemService.healthCheck(); 
            return ResponseEntity.ok("Health Check passed. Downstream system is healthy");
        } catch (ArithmeticException ex) {
            
            return ResponseEntity.ok("Health Check passed. Downstream system is healthy");
        } catch (Exception ex) {
          
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Health Check failed: " + ex.getMessage());
        }
    }


}

	
	
		
