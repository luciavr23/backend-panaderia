package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.DailySpecialDTO;
import com.example.enums.WeekdayEnum;
import com.example.service.DailySpecialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dailySpecials")
@RequiredArgsConstructor
public class DailySpecialController {

	private final DailySpecialService dailySpecialService;

	@GetMapping
	public ResponseEntity<List<DailySpecialDTO>> getAll() {
		return ResponseEntity.ok(dailySpecialService.getAll());
	}

	@GetMapping("/{weekday}")
	public ResponseEntity<DailySpecialDTO> getByWeekday(@PathVariable WeekdayEnum weekday) {
		DailySpecialDTO special = dailySpecialService.getByWeekday(weekday);
		return ResponseEntity.ok(special);
	}

	@PostMapping
	public ResponseEntity<DailySpecialDTO> create(@RequestBody DailySpecialDTO dto) {
		DailySpecialDTO created = dailySpecialService.create(dto);
		return ResponseEntity.ok(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<DailySpecialDTO> update(@PathVariable Long id, @RequestBody DailySpecialDTO dto) {
		DailySpecialDTO updated = dailySpecialService.update(id, dto);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		dailySpecialService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
