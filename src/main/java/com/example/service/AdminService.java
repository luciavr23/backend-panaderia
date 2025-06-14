package com.example.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dto.AdminStatsDTO;
import com.example.dto.PasswordUpdateDTO;
import com.example.dto.ProfileUpdateDTO;
import com.example.entity.FoodOrder;
import com.example.entity.UserAccount;
import com.example.enums.OrderStatusEnum;
import com.example.repository.FoodOrderRepository;
import com.example.repository.UserAccountRepository;

@Service
public class AdminService {

	@Autowired
	private FoodOrderRepository foodOrderRepository;

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public AdminStatsDTO getAdminStats() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
		LocalDateTime todayEnd = now.toLocalDate().atTime(23, 59, 59);

		LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0)
				.withSecond(0);
		LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59)
				.withSecond(59);

		LocalDateTime firstDayOfLastMonth = firstDayOfMonth.minusMonths(1);
		LocalDateTime lastDayOfLastMonth = firstDayOfMonth.minusSeconds(1);
		LocalDateTime start = LocalDate.now().atStartOfDay();
		LocalDateTime end = start.plusDays(1);

		List<FoodOrder> currentMonthOrders = foodOrderRepository.findByCreatedAtBetween(firstDayOfMonth,
				lastDayOfMonth);

		List<FoodOrder> lastMonthOrders = foodOrderRepository.findByCreatedAtBetween(firstDayOfLastMonth,
				lastDayOfLastMonth);

		List<FoodOrder> readyOrdersThisMonth = foodOrderRepository.findByCreatedAtBetweenAndStatus(firstDayOfMonth,
				lastDayOfMonth, OrderStatusEnum.LISTO);

		int totalOrders = currentMonthOrders.size();
		double totalRevenue = calculateTotalRevenue(currentMonthOrders);
		Map<String, Integer> ordersByStatus = calculateOrdersByStatus(currentMonthOrders);

		int avgPreparationTime = (int) readyOrdersThisMonth.stream().filter(o -> o.getEndedAt() != null)
				.mapToLong(o -> Duration.between(o.getCreatedAt(), o.getEndedAt()).toMinutes()).average().orElse(0);

		double growthOrders = calculateGrowth(totalOrders, lastMonthOrders.size());
		double growthRevenue = calculateGrowth(totalRevenue, calculateTotalRevenue(lastMonthOrders));
		AdminStatsDTO.GrowthDTO growth = AdminStatsDTO.GrowthDTO.builder().orders(growthOrders).revenue(growthRevenue)
				.build();

		int pendingToday = foodOrderRepository
				.findByCreatedAtBetweenAndStatus(todayStart, todayEnd, OrderStatusEnum.EN_PREPARACION).size();
		int completedToday = foodOrderRepository
				.findByCreatedAtBetweenAndStatus(todayStart, todayEnd, OrderStatusEnum.LISTO).size();
		int totalOrdersToday = foodOrderRepository.countByCreatedAtBetween(start, end);

		return AdminStatsDTO.builder().totalOrders(totalOrders).totalRevenue(totalRevenue)
				.ordersByStatus(ordersByStatus).growth(growth).avgPreparationTime(avgPreparationTime)
				.pendingOrdersToday(pendingToday).completedOrdersToday(completedToday)
				.totalOrdersToday(totalOrdersToday).build();
	}

	public Map<String, Object> updateProfile(ProfileUpdateDTO profileUpdate) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserAccount admin = userAccountRepository.findByEmail(username)
				.orElseThrow(() -> new RuntimeException("Admin no encontrado"));

		if (profileUpdate.getName() != null)
			admin.setName(profileUpdate.getName());
		if (profileUpdate.getEmail() != null)
			admin.setEmail(profileUpdate.getEmail());

		userAccountRepository.save(admin);

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Perfil actualizado correctamente");
		response.put("admin", admin);
		return response;
	}

	public Map<String, String> changePassword(PasswordUpdateDTO passwordUpdate) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserAccount admin = userAccountRepository.findByEmail(username)
				.orElseThrow(() -> new RuntimeException("Admin no encontrado"));

		if (!passwordEncoder.matches(passwordUpdate.getCurrentPassword(), admin.getPassword())) {
			throw new RuntimeException("Contraseña actual incorrecta");
		}

		admin.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));
		userAccountRepository.save(admin);

		Map<String, String> response = new HashMap<>();
		response.put("message", "Contraseña actualizada correctamente");
		return response;
	}

	private double calculateTotalRevenue(List<FoodOrder> orders) {
		return orders.stream().map(FoodOrder::getTotalPrice).map(BigDecimal::doubleValue).reduce(0.0, Double::sum);
	}

	private Map<String, Integer> calculateOrdersByStatus(List<FoodOrder> orders) {
		Map<String, Integer> statusCount = new HashMap<>();
		for (OrderStatusEnum status : OrderStatusEnum.values()) {
			statusCount.put(status.name(), 0);
		}
		orders.forEach(order -> statusCount.merge(order.getStatus().name(), 1, Integer::sum));
		return statusCount;
	}

	private double calculateGrowth(double current, double previous) {
		if (previous == 0)
			return 100;
		return ((current - previous) / previous) * 100;
	}
}
