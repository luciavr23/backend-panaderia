package com.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.OrderProductDTO;
import com.example.dto.ReviewDTO;
import com.example.entity.FoodOrder;
import com.example.entity.Review;
import com.example.entity.UserAccount;
import com.example.mapper.ReviewMapper;
import com.example.repository.FoodOrderRepository;
import com.example.repository.ReviewRepository;
import com.example.repository.UserAccountRepository;
import com.marketplace.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewMapper reviewMapper;
	private final UserAccountRepository userAccountRepository;
	private final FoodOrderRepository foodOrderRepository;

	public List<ReviewDTO> findAll() {
		return reviewRepository.findAll().stream().map(this::mapReviewToDTOWithProducts).collect(Collectors.toList());
	}

	private ReviewDTO mapReviewToDTOWithProducts(Review review) {
		ReviewDTO dto = reviewMapper.toDTO(review);

		if (review.getOrder() != null && review.getOrder().getItems() != null) {
			List<OrderProductDTO> productList = review
					.getOrder().getItems().stream().map(product -> new OrderProductDTO(product.getId(),
							product.getProduct().getName(), product.getQuantity(), product.getUnitPrice()))
					.collect(Collectors.toList());
			dto.setProducts(productList);
		}

		return dto;
	}

	public List<ReviewDTO> getTopReviews() {
		return reviewRepository.findTopReviews().stream().map(reviewMapper::toDTO).toList();
	}

	public ReviewDTO create(ReviewDTO dto) {
		Review review = reviewMapper.toEntity(dto);

		UserAccount user = userAccountRepository.findById(dto.getUserId())
				.orElseThrow(() -> new BusinessException("Usuario no encontrado con ID: " + dto.getUserId()));

		FoodOrder order = foodOrderRepository.findById(dto.getOrderId())
				.orElseThrow(() -> new BusinessException("Pedido no encontrado con ID: " + dto.getOrderId()));

		review.setUser(user);
		review.setOrder(order);
		review.setCreatedAt(LocalDateTime.now());
		Review saved = reviewRepository.save(review);
		return reviewMapper.toDTO(saved);
	}

	public void delete(Long id) {
		if (!reviewRepository.existsById(id)) {
			throw new BusinessException("No existe reseña con ID: " + id);
		}
		reviewRepository.deleteById(id);
	}

	public java.util.Optional<ReviewDTO> findByOrderId(Long orderId) {
		return reviewRepository.findByOrderId(orderId).map(reviewMapper::toDTO);
	}
}