package com.example.dto;

import com.example.security.CustomUserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
	private String token;
	private CustomUserDetails user;

	public AuthResponse(String token, CustomUserDetails user) {
		this.token = token;
		this.user = user;
	}
}
