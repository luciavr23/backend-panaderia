package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.ReviewDTO;
import com.example.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping
	public ResponseEntity<List<ReviewDTO>> getAllReviews() {
		return ResponseEntity.ok(reviewService.findAll());
	}

	@GetMapping("/limited")
	public List<ReviewDTO> getTopReviews() {
		return reviewService.getTopReviews();
	}

	@PostMapping
	public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO dto) {
		return ResponseEntity.ok(reviewService.create(dto));
	}

	@GetMapping("/order/{orderId}")
	public ResponseEntity<ReviewDTO> getReviewByOrder(@PathVariable Long orderId) {
		return reviewService.findByOrderId(orderId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
		reviewService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
