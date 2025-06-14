package com.example.mapper;

import java.time.LocalTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.dto.ScheduleDTO;
import com.example.entity.Schedule;
import com.example.enums.WeekdayEnum;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

	@Mapping(source = "weekday", target = "dayOfWeek")
	@Mapping(source = "openTime", target = "openTime", qualifiedByName = "formatTime")
	@Mapping(source = "closeTime", target = "closeTime", qualifiedByName = "formatTime")
	ScheduleDTO toDTO(Schedule entity);

	@Mapping(source = "dayOfWeek", target = "weekday", qualifiedByName = "mapToEnum")
	@Mapping(source = "openTime", target = "openTime", qualifiedByName = "parseTime")
	@Mapping(source = "closeTime", target = "closeTime", qualifiedByName = "parseTime")
	Schedule toEntity(ScheduleDTO dto);

	@Named("formatTime")
	default String formatTime(LocalTime time) {
		return time != null ? time.toString() : null;
	}

	@Named("parseTime")
	default LocalTime parseTime(String time) {
		return time != null ? LocalTime.parse(time) : null;
	}

	@Named("mapToEnum")
	default WeekdayEnum mapToEnum(String day) {
		return WeekdayEnum.valueOf(day.toUpperCase());
	}
}
