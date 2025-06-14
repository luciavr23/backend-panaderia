package com.example.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsDTO {
	private int totalOrders;
	private double totalRevenue;
	private Map<String, Integer> ordersByStatus;
	private GrowthDTO growth;
	private int avgPreparationTime;
	private int pendingOrdersToday;
	private int completedOrdersToday;
	private int totalOrdersToday;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class GrowthDTO {
		private double orders;
		private double revenue;
	}
}