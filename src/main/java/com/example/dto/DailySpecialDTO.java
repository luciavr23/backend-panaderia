package com.example.dto;

import com.example.enums.WeekdayEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailySpecialDTO {
	private Long id;
	private Long productId;
	private String productName;
	private WeekdayEnum weekday;
}
