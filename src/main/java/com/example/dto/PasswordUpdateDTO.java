package com.example.dto;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
	private String currentPassword;
	private String newPassword;
}