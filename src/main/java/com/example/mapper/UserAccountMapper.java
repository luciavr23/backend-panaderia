package com.example.mapper;

import org.mapstruct.Mapper;

import com.example.dto.UserAccountDTO;
import com.example.entity.UserAccount;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {
	UserAccountDTO toDTO(UserAccount user);

	UserAccount toEntity(UserAccountDTO userAccountDTO);

}