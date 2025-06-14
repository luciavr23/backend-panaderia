package com.example.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodOrderPaymentDTO {
	private List<OrderedItemDTO> products;
	private String paymentIntentId;

	@Override
	public String toString() {
		String peticion = "paymentIntentId" + paymentIntentId;
		for (OrderedItemDTO prod : products) {
			peticion.concat(",producto:" + prod.getProductId() + "-" + prod.getQuantity());
		}
		return peticion;
	}
}
