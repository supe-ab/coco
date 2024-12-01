package com.assignment.product_service.eo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.assignment.product_service.dao.ItemRepository;
import com.assignment.product_service.dto.ItemDTO;
import com.assignment.product_service.entity.Item;
import com.assignment.product_service.mapper.ItemMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemEO {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemDTO createItem(ItemDTO itemDTO) {
        Item entity = itemMapper.dtoToEntity(itemDTO); 
        Item savedEntity = itemRepository.save(entity); 
        return itemMapper.entityToDTO(savedEntity); 
    }

    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(itemMapper::entityToDTO) 
                .collect(Collectors.toList());
    }

    public void healthCheck() throws Exception {
        try {
            
            itemRepository.findById(0L);  

           
            throw new ArithmeticException("HealthCheck passed: Downstream system is healthy");

        } catch (ArithmeticException e) {
            
            throw new ArithmeticException(e.getMessage()); 
        } catch (Exception e) {
            
            throw new Exception("HealthCheck failed: " + e.getMessage());
        }
    }



		
		
		
		
	}
