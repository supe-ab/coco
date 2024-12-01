package com.assignment.product_service.mapper;

import org.mapstruct.Mapper;

import com.assignment.product_service.dto.ItemDTO;
import com.assignment.product_service.entity.Item;
import com.assignment.product_service.vo.ItemVO;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDTO voToDTO(ItemVO vo);
    ItemVO dtoToVO(ItemDTO dto);

    ItemDTO entityToDTO(Item entity);
    Item dtoToEntity(ItemDTO dto);
}