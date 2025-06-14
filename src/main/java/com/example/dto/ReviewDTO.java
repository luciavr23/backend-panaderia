package com.example.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
	private Long id;
	private Long userId;
	private String userName;
	private Double stars;
	private String comment;
	private LocalDateTime createdAt;
	private Long orderId;
	private List<OrderProductDTO> products;
}
