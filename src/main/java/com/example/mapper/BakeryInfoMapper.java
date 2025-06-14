package com.example.mapper;

import org.mapstruct.Mapper;

import com.example.dto.BakeryInfoDTO;
import com.example.entity.BakeryInfo;

@Mapper(componentModel = "spring")
public interface BakeryInfoMapper {
	BakeryInfoDTO toDTO(BakeryInfo entity);

	BakeryInfo toEntity(BakeryInfoDTO dto);
}
