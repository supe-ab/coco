package com.assignment.product_service.service;

import java.util.List;

import com.assignment.product_service.vo.ItemVO;

public interface ItemService {

	ItemVO createItem(ItemVO itemVO);      
   
    List<ItemVO> getAllItems();

	void healthCheck() throws Exception;             

}