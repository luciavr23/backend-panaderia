package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderedItemDTO {
	private Long productId;
	private Integer quantity;

	@Override
	public String toString() {
		return "OrderedItemDTO [productId=" + productId + ", quantity=" + quantity + "]";
	}
}
