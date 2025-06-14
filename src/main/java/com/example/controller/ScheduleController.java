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

import com.example.dto.ScheduleDTO;
import com.example.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

	private final ScheduleService scheduleService;

	@GetMapping
	public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
		return ResponseEntity.ok(scheduleService.getAllSchedules());
	}

	@PostMapping
	public ResponseEntity<ScheduleDTO> createSchedule(@RequestBody ScheduleDTO dto) {
		return ResponseEntity.ok(scheduleService.createSchedule(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Long id, @RequestBody ScheduleDTO dto) {
		return ResponseEntity.ok(scheduleService.updateSchedule(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
		scheduleService.deleteSchedule(id);
		return ResponseEntity.noContent().build();
	}
}