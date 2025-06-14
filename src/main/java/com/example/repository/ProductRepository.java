package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Product;
import com.example.enums.WeekdayEnum;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByAvailableTrue();

	List<Product> findByPopularTrue();

	Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

	Page<Product> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String name, Pageable pageable);

	Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Product> findByAvailableTrue(Pageable pageable);

	Page<Product> findByAvailableTrueAndCategoryId(Long categoryId, Pageable pageable);

	Page<Product> findByAvailableTrueAndCategoryIdAndNameContainingIgnoreCase(Long categoryId, String name,
			Pageable pageable);

	Page<Product> findByAvailableTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

	@Query("""
			    SELECT p FROM Product p
			    LEFT JOIN p.dailySpecial d
			    WHERE p.available = true
			    AND (d IS NULL OR d.weekday = :today)
			""")
	Page<Product> findAvailableForToday(@Param("today") WeekdayEnum today, Pageable pageable);

	@Query("""
			    SELECT p FROM Product p
			    LEFT JOIN p.dailySpecial d
			    WHERE p.available = true
			    AND (d IS NULL OR d.weekday = :today)
			    AND LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
			""")
	Page<Product> findAvailableForTodayWithSearch(@Param("today") WeekdayEnum today, @Param("search") String search,
			Pageable pageable);

	@Query("""
			    SELECT p FROM Product p
			    LEFT JOIN p.dailySpecial d
			    WHERE p.available = true
			    AND p.category.id = :categoryId
			    AND (d IS NULL OR d.weekday = :today)
			""")
	Page<Product> findAvailableForTodayWithCategory(@Param("categoryId") Long categoryId,
			@Param("today") WeekdayEnum today, Pageable pageable);

	@Query("""
			    SELECT p FROM Product p
			    LEFT JOIN p.dailySpecial d
			    WHERE p.available = true
			    AND p.category.id = :categoryId
			    AND (d IS NULL OR d.weekday = :today)
			    AND LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
			""")
	Page<Product> findAvailableForTodayWithCategoryAndSearch(@Param("categoryId") Long categoryId,
			@Param("search") String search, @Param("today") WeekdayEnum today, Pageable pageable);

}
