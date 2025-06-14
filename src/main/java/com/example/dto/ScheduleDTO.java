package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {
	private Long id;
	private String dayOfWeek;
	private String openTime;
	private String closeTime;
	private boolean closed;
}
