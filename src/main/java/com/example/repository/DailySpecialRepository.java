package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.DailySpecial;
import com.example.enums.WeekdayEnum;

@Repository
public interface DailySpecialRepository extends JpaRepository<DailySpecial, Long> {
	Optional<DailySpecial> findByWeekday(WeekdayEnum weekday);

	@Query(value = """
			SELECT product_id
			FROM daily_special
			WHERE weekday <> CAST(:today AS weekday_enum)
			""", nativeQuery = true)
	List<Long> findProductIdsNotForToday(@Param("today") String today);

}
