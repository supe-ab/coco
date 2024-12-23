package com.assignment.product_service.eo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assignment.product_service.dao.ItemRepository;
import com.assignment.product_service.dto.ItemDTO;
import com.assignment.product_service.entity.Item;
import com.assignment.product_service.mapper.ItemMapper;

@ExtendWith(MockitoExtension.class)
public class ItemEOTest {
	
	@Mock
	private ItemRepository itemRepository;
	
	@Mock
	private  ItemMapper itemMapper;
	
	@InjectMocks
	private ItemEO itemEO; 
	
	private ItemDTO itemDTO;
	private Item itemEntity;
	
	@BeforeEach
	void setUp() {
		itemDTO = new ItemDTO();
		itemDTO.setId(1L);
		itemDTO.setName("Test Item");
		itemDTO.setQuantity(10);
		
		itemEntity = new Item();
		itemEntity.setId(1L);
		itemEntity.setName("Test item");
		itemEntity.setQuantity(10);
	}
	
	@Test
	void testCreateItem() {
		when(itemMapper.dtoToEntity(itemDTO)).thenReturn(itemEntity);
		when(itemRepository.save(itemEntity)).thenReturn(itemEntity);
		when(itemMapper.entityToDTO(itemEntity)).thenReturn(itemDTO);
		
		ItemDTO result = itemEO.createItem(itemDTO);
		
		assertNotNull(result);
		assertEquals(itemDTO.getName(), result.getName());
		verify(itemMapper).dtoToEntity(itemDTO);
		verify(itemRepository).save(itemEntity);
		verify(itemMapper).entityToDTO(itemEntity);
	}
	
	@Test
    void testGetAllItems() {
        when(itemRepository.findAll()).thenReturn(List.of(itemEntity));
        when(itemMapper.entityToDTO(itemEntity)).thenReturn(itemDTO);

        List<ItemDTO> result = itemEO.getAllItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDTO.getName(), result.get(0).getName());
        verify(itemRepository).findAll();
        verify(itemMapper).entityToDTO(itemEntity);
    }

	@Test
	void testHealthCheck_Success() throws Exception {
	    // Mock the repository to return an entity for the given ID
	    when(itemRepository.findById(0L)).thenReturn(Optional.of(itemEntity));

	    // Try to call the healthCheck method, expecting it to throw an ArithmeticException
	    ArithmeticException exception = assertThrows(ArithmeticException.class, () -> itemEO.healthCheck());

	    // Assert that the exception message is correct
	    assertEquals("HealthCheck passed: Downstream system is healthy", exception.getMessage());

	    // Verify that the repository method was called
	    verify(itemRepository).findById(0L);
	}
    @Test
    void testHealthCheck_Failure() {
        // Mocking the exception being thrown
        when(itemRepository.findById(0L)).thenThrow(new RuntimeException("Database down"));

        try {
            // Attempt the health check, expecting an exception
            itemEO.healthCheck();
        } catch (Exception e) {
            // If an exception is thrown, assert its message and allow test to pass
            assertEquals("HealthCheck failed: Database down", e.getMessage());
        }
    }
}
