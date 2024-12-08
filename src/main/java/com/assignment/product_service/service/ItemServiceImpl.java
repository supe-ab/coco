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

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemBO itemBO;

    @Transactional
    @CircuitBreaker(name = "createItemService", fallbackMethod = "createItemFallback")
    public ItemVO createItem(ItemVO itemVO) throws InterruptedException {
    	//Thread.sleep(7000);
        ItemDTO itemDTO = itemMapper.voToDTO(itemVO); // VO -> DTO
        ItemDTO createdDTO = itemBO.createItem(itemDTO); // Call BO
        return itemMapper.dtoToVO(createdDTO); // DTO -> VO
    }

    @Transactional(readOnly = true)
    public List<ItemVO> getAllItems() {
        return itemBO.getAllItems() // Call BO
                .stream()
                .map(itemMapper::dtoToVO) // DTO -> VO
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public void healthCheck() throws Exception {
    	itemBO.healthCheck();
    }

	@Override
	public void createItem(String name, int quantity) {
		// TODO Auto-generated method stub
		
	}
	
	private ItemVO createItemFallback(ItemVO itemVO, Throwable throwable) {
	    

	    return new ItemVO(); // Or implement more sophisticated fallback logic
	}

	
}