package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.FoodOrderDTO;
import com.example.entity.FoodOrder;

@Mapper(componentModel = "spring", uses = { OrderProductMapper.class })
public interface FoodOrderMapper {

	@Mapping(source = "createdAt", target = "orderDate")
	@Mapping(source = "totalPrice", target = "total")
	@Mapping(source = "items", target = "products")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "user.name", target = "clientName")
	@Mapping(source = "user.surname", target = "clientSurname") // ← AÑADIR ESTA LÍNEA
	FoodOrderDTO toDto(FoodOrder entity);

	@Mapping(source = "orderDate", target = "createdAt")
	@Mapping(source = "total", target = "totalPrice")
	@Mapping(source = "products", target = "items")
	@Mapping(source = "status", target = "status")
	FoodOrder toEntity(FoodOrderDTO dto);
}
