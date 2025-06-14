package com.example.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.AllergenDTO;
import com.example.service.AllergenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/allergens")
@RequiredArgsConstructor
public class AllergenController {

	private final AllergenService allergenService;

	@GetMapping
	public ResponseEntity<List<AllergenDTO>> getAllAllergens() {
		List<AllergenDTO> allergens = allergenService.getAllAllergens();
		return ResponseEntity.ok(allergens);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AllergenDTO> getAllergenById(@PathVariable Long id) {
		AllergenDTO allergen = allergenService.getAllergenById(id);
		return ResponseEntity.ok(allergen);
	}

	@PostMapping
	public ResponseEntity<AllergenDTO> createAllergen(@RequestBody AllergenDTO allergenDTO) {
		AllergenDTO createdAllergen = allergenService.createAllergen(allergenDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdAllergen);
	}

	@PutMapping("/{id}")
	public ResponseEntity<AllergenDTO> updateAllergen(@PathVariable Long id, @RequestBody AllergenDTO allergenDTO) {
		AllergenDTO updatedAllergen = allergenService.updateAllergen(id, allergenDTO);
		return ResponseEntity.ok(updatedAllergen);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAllergen(@PathVariable Long id) {
		allergenService.deleteAllergen(id);
		return ResponseEntity.noContent().build();
	}
}
