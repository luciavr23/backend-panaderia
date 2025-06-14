package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.DailySpecialDTO;
import com.example.entity.DailySpecial;

@Mapper(componentModel = "spring")
public interface DailySpecialMapper {

	@Mapping(source = "product.id", target = "productId")
	@Mapping(source = "product.name", target = "productName")
	DailySpecialDTO toDTO(DailySpecial dailySpecial);

	@Mapping(target = "product", ignore = true)
	DailySpecial toEntity(DailySpecialDTO dto);
}
