package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOrderDTO {
	private Long id;
	private LocalDateTime orderDate;
	private String status;
	private BigDecimal total;
	private String orderNumber;
	private String clientName;
	private String clientSurname;
	private LocalDateTime endedAt;

	private List<OrderProductDTO> products;
	private ReviewDTO review;
}