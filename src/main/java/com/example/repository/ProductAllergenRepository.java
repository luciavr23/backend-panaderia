package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.ProductAllergen;

@Repository
public interface ProductAllergenRepository extends JpaRepository<ProductAllergen, Long> {
	List<ProductAllergen> findByProductId(Long productId);
}
