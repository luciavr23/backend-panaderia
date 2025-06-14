package com.example.mapper;

import org.mapstruct.Mapper;

import com.example.dto.ProductCategoryDTO;
import com.example.entity.ProductCategory;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
	ProductCategoryDTO toDTO(ProductCategory category);

	ProductCategory toEntity(ProductCategoryDTO productCategoryDTO);
}
