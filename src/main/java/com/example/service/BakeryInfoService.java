package com.example.service;

import org.springframework.stereotype.Service;

import com.example.dto.BakeryInfoDTO;
import com.example.mapper.BakeryInfoMapper;
import com.example.repository.BakeryInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BakeryInfoService {

	private final BakeryInfoRepository bakeryInfoRepository;
	private final BakeryInfoMapper bakeryInfoMapper;

	public BakeryInfoDTO getBakeryInfo() {
		return bakeryInfoRepository.findAll().stream().findFirst().map(bakeryInfoMapper::toDTO)
				.orElseThrow(() -> new RuntimeException("No bakery info found"));
	}

	public BakeryInfoDTO createBakeryInfo(BakeryInfoDTO dto) {
		var entity = bakeryInfoMapper.toEntity(dto);
		var saved = bakeryInfoRepository.save(entity);
		return bakeryInfoMapper.toDTO(saved);
	}

	public BakeryInfoDTO updateBakeryInfo(Long id, BakeryInfoDTO dto) {
		var existing = bakeryInfoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("No se encontró la información con ID: " + id));

		if (dto.getName() != null)
			existing.setName(dto.getName());
		if (dto.getStreet() != null)
			existing.setStreet(dto.getStreet());
		if (dto.getCity() != null)
			existing.setCity(dto.getCity());
		if (dto.getPostalCode() != null)
			existing.setPostalCode(dto.getPostalCode());
		if (dto.getCountry() != null)
			existing.setCountry(dto.getCountry());
		if (dto.getMunicipality() != null)
			existing.setMunicipality(dto.getMunicipality());
		if (dto.getProvince() != null)
			existing.setProvince(dto.getProvince());
		if (dto.getPhone() != null)
			existing.setPhone(dto.getPhone());
		if (dto.getEmail() != null)
			existing.setEmail(dto.getEmail());
		if (dto.getLocationUrl() != null)
			existing.setLocationUrl(dto.getLocationUrl());
		if (dto.getFacebookUrl() != null)
			existing.setFacebookUrl(dto.getFacebookUrl());

		var updated = bakeryInfoRepository.save(existing);
		return bakeryInfoMapper.toDTO(updated);
	}

	public void deleteBakeryInfo(Long id) {
		if (!bakeryInfoRepository.existsById(id)) {
			throw new RuntimeException("No se encontró la información con ID: " + id);
		}
		bakeryInfoRepository.deleteById(id);
	}

}
