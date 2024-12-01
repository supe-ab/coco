package com.assignment.product_service.bo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assignment.product_service.dto.ItemDTO;
import com.assignment.product_service.eo.ItemEO;

@ExtendWith(MockitoExtension.class)
public class ItemBOTest {

    @Mock
    private ItemEO itemEO;

    @InjectMocks
    private ItemBO itemBO; 

    private ItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        itemDTO = new ItemDTO();
        itemDTO.setId(1L);
        itemDTO.setName("Test Item");
        itemDTO.setQuantity(10);
    }

    @Test
    void testCreateItem() {
        
        when(itemEO.createItem(itemDTO)).thenReturn(itemDTO);

       
        ItemDTO result = itemBO.createItem(itemDTO);

        
        assertNotNull(result);
        assertEquals(itemDTO.getName(), result.getName());
        verify(itemEO).createItem(itemDTO);
    }

    @Test
    void testGetAllItems() {
      
        when(itemEO.getAllItems()).thenReturn(List.of(itemDTO));

     
        List<ItemDTO> result = itemBO.getAllItems();

     
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDTO.getName(), result.get(0).getName());
        verify(itemEO).getAllItems();
    }

    @Test
    void testHealthCheck() throws Exception {
       
        doNothing().when(itemEO).healthCheck();

        
        itemBO.healthCheck();

       
        verify(itemEO).healthCheck();
    }
}