package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.ProductCategoryDTO;
import com.example.service.ProductCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

	private final ProductCategoryService categoryService;
	@Value("${pagination.categories-per-page}")
	private int categoriesPerPage;

	@GetMapping
	public ResponseEntity<Page<ProductCategoryDTO>> getAllCategories(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "name") String sortBy, @RequestParam(defaultValue = "asc") String direction) {

		if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
			return ResponseEntity.badRequest().build();
		}

		Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
		Pageable pageable = PageRequest.of(page, categoriesPerPage, Sort.by(sortDirection, sortBy));

		Page<ProductCategoryDTO> categories = categoryService.getAllCategories(pageable);

		return ResponseEntity.ok(categories);
	}

	@GetMapping("/all")
	public ResponseEntity<List<ProductCategoryDTO>> getAll() {
		return ResponseEntity.ok(categoryService.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductCategoryDTO> getCategoryById(@PathVariable Long id) {
		return ResponseEntity.ok(categoryService.getCategoryById(id));
	}

	@PostMapping
	public ResponseEntity<ProductCategoryDTO> createCategory(@RequestBody ProductCategoryDTO dto) {
		return ResponseEntity.ok(categoryService.createCategory(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductCategoryDTO> updateCategory(@PathVariable Long id,
			@RequestBody ProductCategoryDTO dto) {
		return ResponseEntity.ok(categoryService.updateCategory(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}
}
