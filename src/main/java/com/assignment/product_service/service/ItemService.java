package com.assignment.product_service.service;

import java.util.List;

import com.assignment.product_service.vo.ItemVO;

public interface ItemService {

	ItemVO createItem(ItemVO itemVO) throws InterruptedException;      
   
    List<ItemVO> getAllItems();

	void healthCheck() throws Exception;

	void createItem(String name, int quantity);             

}