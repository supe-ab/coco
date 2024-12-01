package com.assignment.product_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.assignment.product_service.service.ItemService;
import com.assignment.product_service.vo.ItemVO;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ItemService itemService;
	
	private ItemVO itemVO;
	
	@BeforeEach
	void setUp() {
		itemVO = new ItemVO();
		itemVO.setId(1L);
		itemVO.setName("Test Item");
		itemVO.setQuantity(10);
	}
	
	@Test
	void testGetAllItems() throws Exception {
	    List<ItemVO> items = List.of(new ItemVO());
	    when(itemService.getAllItems()).thenReturn(items);

	    mockMvc.perform(get("/api/items"))  // Add the leading slash here
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].name").value("Test Item"))
	        .andExpect(jsonPath("$[0].quantity").value(10));
	}

	@Test
	void testCreateItem() throws Exception {
	    when(itemService.createItem(any(ItemVO.class))).thenReturn(itemVO);

	    mockMvc.perform(post("/api/items")  // Add the leading slash here
	            .contentType(MediaType.APPLICATION_JSON)
	            .content("{\"name\":\"Test Item\",\"quantity\":10}")
	    )
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.name").value("Test Item"))
	    .andExpect(jsonPath("$.quantity").value(10));

	    verify(itemService).createItem(any(ItemVO.class));
	}


}