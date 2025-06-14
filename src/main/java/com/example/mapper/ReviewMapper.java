package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.ReviewDTO;
import com.example.entity.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

	@Mapping(source = "user.name", target = "userName")
	@Mapping(source = "order.id", target = "orderId")
	ReviewDTO toDTO(Review review);

	@Mapping(target = "user", ignore = true)
	@Mapping(target = "order", ignore = true)
	Review toEntity(ReviewDTO dto);
}
