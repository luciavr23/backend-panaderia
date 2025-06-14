package com.example.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.dto.AllergenDTO;
import com.example.entity.Allergen;

@Mapper(componentModel = "spring")
public interface AllergenMapper {
	AllergenDTO toDTO(Allergen allergen);

	Allergen toEntity(AllergenDTO allergenDTO);

	List<Allergen> toEntityList(List<AllergenDTO> allergenDTOList);
}
