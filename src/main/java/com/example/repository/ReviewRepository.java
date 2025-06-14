package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	Optional<Review> findByOrderId(Long orderId);

	@Query(value = """
			    SELECT * FROM review
			    WHERE stars > 3.5
			    ORDER BY RANDOM()
			    LIMIT 10
			""", nativeQuery = true)
	List<Review> findTopReviews();

}
