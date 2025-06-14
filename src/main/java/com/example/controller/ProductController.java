package com.example.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

import com.example.dto.OrderProductDTO;
import com.example.dto.ProductDTO;
import com.example.service.ProductService;
import com.marketplace.exception.InsufficientStockException;
import com.marketplace.exception.ProductNotFoundException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	@Value("${pagination.products-per-page}")
	private int productsPerPage;

	private final ProductService productService;

	@GetMapping("/all")
	public ResponseEntity<List<ProductDTO>> getAllProducts() {
		return ResponseEntity.ok(productService.getAllProductsWithoutCategory());
	}

	@PostMapping("/validate-stock")
	public ResponseEntity<?> validateStock(@RequestBody List<OrderProductDTO> items) {
		try {
			productService.validateStock(items);
			return ResponseEntity.ok().build();
		} catch (InsufficientStockException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
		}
	}

	@GetMapping
	public ResponseEntity<Page<ProductDTO>> getAllProducts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "name") String sortBy, @RequestParam(defaultValue = "asc") String direction,
			@RequestParam(required = false) String search, @RequestParam(required = false) Long categoryId) {

		if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
			return ResponseEntity.badRequest().build();
		}

		Page<ProductDTO> products = productService.getAllProducts(page, productsPerPage, sortBy, direction, search,
				categoryId);

		return ResponseEntity.ok(products);
	}

	@GetMapping("/available")
	public ResponseEntity<Page<ProductDTO>> getAvailableProducts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "name") String sortBy, @RequestParam(defaultValue = "asc") String direction,
			@RequestParam(required = false) String search, @RequestParam(required = false) Long categoryId) {

		if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
			return ResponseEntity.badRequest().build();
		}

		Page<ProductDTO> products = productService.getAvailableProducts(page, productsPerPage, sortBy, direction,
				search, categoryId);

		return ResponseEntity.ok(products);
	}

	@GetMapping("/available/test")
	public ResponseEntity<List<ProductDTO>> testAvailableToday(@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) String search) {

		return ResponseEntity
				.ok(productService.getAvailableProducts(0, 100, "name", "asc", search, categoryId).getContent());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getProductById(id));
	}

	@GetMapping("/popular")
	public ResponseEntity<List<ProductDTO>> getPopularProducts() {
		return ResponseEntity.ok(productService.getPopularProducts());
	}

	@PostMapping
	public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
		ProductDTO created = productService.createProduct(dto);
		return ResponseEntity.ok(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
		ProductDTO updated = productService.updateProduct(id, dto);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}
}
