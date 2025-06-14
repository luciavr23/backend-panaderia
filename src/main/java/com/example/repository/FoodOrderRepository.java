package com.example.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.FoodOrder;
import com.example.enums.OrderStatusEnum;

@Repository
public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
	List<FoodOrder> findByUserId(Long userId);

	List<FoodOrder> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	List<FoodOrder> findByCreatedAtBetweenAndStatus(LocalDateTime start, LocalDateTime end, OrderStatusEnum status);

	int countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
