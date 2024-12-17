package com.assignment.product_service.bo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.assignment.product_service.dto.ItemDTO;
import com.assignment.product_service.eo.ItemEO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemBO {
    private final ItemEO itemEO;

    public ItemDTO createItem(ItemDTO itemDTO) throws InterruptedException {
    	//Thread.sleep(8000); // Simulate a slow call

       
        return itemEO.createItem(itemDTO); 
    }

    public List<ItemDTO> getAllItems() {
        
        return itemEO.getAllItems(); 
    }

	public void healthCheck() throws Exception {
		itemEO.healthCheck();
		
		
	}
}