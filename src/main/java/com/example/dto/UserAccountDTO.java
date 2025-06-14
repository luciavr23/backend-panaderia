package com.example.dto;

import lombok.Data;

@Data
public class UserAccountDTO {
	private Long id;
	private String name;
	private String surname;
	private String email;
	private String oldPassword;
	private String password;
	private String role;
	private String phoneNumber;
	private String profileImage;

}
