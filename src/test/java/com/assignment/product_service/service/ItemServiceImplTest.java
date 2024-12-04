package com.assignment.product_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assignment.product_service.bo.ItemBO;
import com.assignment.product_service.dto.ItemDTO;
import com.assignment.product_service.mapper.ItemMapper;
import com.assignment.product_service.vo.ItemVO;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
	
	@Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemBO itemBO;

    @InjectMocks
    private ItemServiceImpl itemService; // Service under test

    private ItemVO itemVO;
    private ItemDTO itemDTO;
	
	@BeforeEach
	void setup() {
		itemVO = new ItemVO();
		itemVO.setName("Test Item");
		itemVO.setQuantity(10);
		
		itemDTO = new ItemDTO();
		itemDTO.setName("Test Item");
		itemDTO.setQuantity(10);
	}
	
	@Test
	void testCreateItem() throws InterruptedException {
		
		when(itemMapper.voToDTO(itemVO)).thenReturn(itemDTO);
		when(itemBO.createItem(itemDTO)).thenReturn(itemDTO);
		when(itemMapper.dtoToVO(itemDTO)).thenReturn(itemVO);
		
		ItemVO result = itemService.createItem(itemVO);
		
		assertNotNull(result);
		assertEquals(itemVO.getName(),result.getName());
		assertEquals(itemVO.getQuantity(),result.getQuantity());
		
		verify(itemMapper).voToDTO(itemVO);
		verify(itemBO).createItem(itemDTO);
		verify(itemMapper).dtoToVO(itemDTO);
		
	}
	
	@Test
	void testGetAllItems() {
		
		List<ItemDTO> mockItemDTOs = List.of(itemDTO);
		List<ItemVO> mockItemVOs = List.of(itemVO);
		
		when(itemBO.getAllItems()).thenReturn(mockItemDTOs);
		when(itemMapper.dtoToVO(itemDTO)).thenReturn(itemVO);
		
		List<ItemVO> result = itemService.getAllItems();
		
		assertNotNull(result);
		assertEquals(1,result.size());
		assertEquals(itemVO.getName(),result.get(0).getName());
		
		
		verify(itemBO).getAllItems();
		verify(itemMapper).dtoToVO(itemDTO);
		
	
	}
	
	
	
	

}