package com.example.mapper;

import org.mapstruct.Mapper;

import com.example.dto.ProductImageDTO;
import com.example.entity.ProductImage;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

	ProductImageDTO toDTO(ProductImage image);

	ProductImage toEntity(ProductImageDTO dto);

}
