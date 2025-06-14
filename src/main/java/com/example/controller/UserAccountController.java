package com.example.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.UserAccountDTO;
import com.example.service.UserAccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAccountController {

	private final UserAccountService userAccountService;

	@GetMapping("/me")
	public ResponseEntity<UserAccountDTO> getUserProfile(Authentication authentication) {
		return ResponseEntity.ok(userAccountService.getUserProfile(authentication.getName()));
	}

	@PutMapping("/me")
	public ResponseEntity<?> updateUserProfile(@RequestBody UserAccountDTO updatedUser, Authentication authentication) {
		if (authentication == null || authentication.getName() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
		}

		String currentEmail = authentication.getName();

		Map<String, Object> response = userAccountService.updateUserProfileWithTokenCheck(currentEmail, updatedUser);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserAccountDTO userdto) {
		Map<String, Object> response = userAccountService.registerUser(userdto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		userAccountService.generateResetTokenAndSendEmail(email);
		return ResponseEntity.ok(Map.of("message", "Si el email existe, se ha enviado un enlace de recuperación"));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
		String token = body.get("token");
		String newPassword = body.get("newPassword");
		userAccountService.resetPassword(token, newPassword);
		return ResponseEntity.ok(Map.of("message", "Contraseña restablecida correctamente"));
	}

}