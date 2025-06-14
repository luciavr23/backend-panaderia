package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.AdminStatsDTO;
import com.example.dto.PasswordUpdateDTO;
import com.example.dto.ProfileUpdateDTO;
import com.example.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@GetMapping("/stats")
	public ResponseEntity<AdminStatsDTO> getAdminStats() {
		return ResponseEntity.ok(adminService.getAdminStats());
	}

	@PutMapping("/profile")
	public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateDTO profileUpdate) {
		return ResponseEntity.ok(adminService.updateProfile(profileUpdate));
	}

	@PutMapping("/password")
	public ResponseEntity<?> changePassword(@RequestBody PasswordUpdateDTO passwordUpdate) {
		return ResponseEntity.ok(adminService.changePassword(passwordUpdate));
	}
}