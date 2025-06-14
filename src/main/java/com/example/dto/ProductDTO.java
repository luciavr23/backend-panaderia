package com.example.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private Boolean available;
	private Integer stock;
	private Long categoryId;
	private Boolean popular;
	private String imageUrl;
	private List<AllergenDTO> allergens;
	private List<ProductImageDTO> images;
}
