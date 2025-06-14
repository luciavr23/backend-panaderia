package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BakeryInfoDTO {
	private Long id;
	private String name;
	private String phone;
	private String email;
	private String locationUrl;
	private String facebookUrl;
	private String imageUrl;
	private String street;
	private String city;
	private String postalCode;
	private String municipality;
	private String province;
	private String country;

}
