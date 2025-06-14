package com.example.controller;

import java.net.URI;
import java.util.List;

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

import com.example.dto.ProductImageDTO;
import com.example.service.ProductImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImageController {
	private final ProductImageService productImageService;

	@GetMapping("/product/{productId}")
	public ResponseEntity<List<ProductImageDTO>> getImagesByProduct(@PathVariable Long productId) {
		return ResponseEntity.ok(productImageService.getImagesByProductId(productId));
	}

	@PostMapping
	public ResponseEntity<ProductImageDTO> addImage(@RequestBody ProductImageDTO dto) {
		ProductImageDTO saved = productImageService.addImage(dto);
		return ResponseEntity.created(URI.create("/product-images/" + saved.getId())).body(saved);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
		productImageService.deleteImage(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/order")
	public ResponseEntity<ProductImageDTO> updateOrder(@PathVariable Long id, @RequestParam int order) {
		return ResponseEntity.ok(productImageService.updateImageOrder(id, order));
	}
}
