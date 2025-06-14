package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.ScheduleDTO;
import com.example.entity.Schedule;
import com.example.mapper.ScheduleMapper;
import com.example.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final ScheduleMapper scheduleMapper;

	public List<ScheduleDTO> getAllSchedules() {
		return scheduleRepository.findAll().stream().map(scheduleMapper::toDTO).collect(Collectors.toList());
	}

	public ScheduleDTO createSchedule(ScheduleDTO dto) {
		Schedule entity = scheduleMapper.toEntity(dto);
		Schedule saved = scheduleRepository.save(entity);
		return scheduleMapper.toDTO(saved);
	}

	public ScheduleDTO updateSchedule(Long id, ScheduleDTO dto) {
		Schedule existing = scheduleRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));

		existing.setWeekday(scheduleMapper.mapToEnum(dto.getDayOfWeek()));
		existing.setOpenTime(scheduleMapper.parseTime(dto.getOpenTime()));
		existing.setCloseTime(scheduleMapper.parseTime(dto.getCloseTime()));
		existing.setClosed(dto.isClosed());

		Schedule updated = scheduleRepository.save(existing);
		return scheduleMapper.toDTO(updated);
	}

	public void deleteSchedule(Long id) {
		if (!scheduleRepository.existsById(id)) {
			throw new RuntimeException("Horario no encontrado con ID: " + id);
		}
		scheduleRepository.deleteById(id);
	}
}
