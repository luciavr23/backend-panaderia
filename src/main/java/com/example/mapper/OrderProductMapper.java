package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.OrderProductDTO;
import com.example.entity.OrderProduct;

@Mapper(componentModel = "spring")
public interface OrderProductMapper {

	@Mapping(source = "product.name", target = "productName")
	@Mapping(source = "unitPrice", target = "price")
	OrderProductDTO toDto(OrderProduct entity);

	default OrderProduct toEntity(OrderProductDTO dto) {
		throw new UnsupportedOperationException("OrderProductDTO is output-only; mapping to entity is not supported.");
	}
}
