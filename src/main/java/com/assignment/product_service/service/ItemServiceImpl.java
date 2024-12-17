package com.assignment.product_service.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.assignment.product_service.bo.ItemBO;
import com.assignment.product_service.dto.ItemDTO;
import com.assignment.product_service.mapper.ItemMapper;
import com.assignment.product_service.vo.ItemVO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemBO itemBO;

    @Transactional
    @CircuitBreaker(name = "createItemService", fallbackMethod = "createItemFallback")
    public ItemVO createItem(ItemVO itemVO) {
        if (itemVO == null || itemVO.getName() == null || itemVO.getName().isEmpty()) {
            log.error("Invalid item: Cannot create item with null or empty name");
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }

        try {
            ItemDTO itemDTO = itemMapper.voToDTO(itemVO); // VO -> DTO
            ItemDTO createdDTO = itemBO.createItem(itemDTO); // Call BO
            return itemMapper.dtoToVO(createdDTO); // DTO -> VO
        } catch (Exception e) {
            log.error("Error creating item", e);
            throw new RuntimeException("Failed to create item", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ItemVO> getAllItems() {
        return itemBO.getAllItems() // Call BO
                .stream()
                .map(itemMapper::dtoToVO) // DTO -> VO
                .collect(Collectors.toList());
    }

   
    ItemVO createItemFallback(ItemVO itemVO, Throwable throwable) {
        log.error("Fallback method called due to: {}", throwable.getMessage());
        
        
        ItemVO fallbackItem = new ItemVO();
        fallbackItem.setName("Fallback Item");
        fallbackItem.setQuantity(0);
        
        return fallbackItem;
    }

	@Override
	public void healthCheck() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createItem(String name, int quantity) {
		// TODO Auto-generated method stub
		
	}
}