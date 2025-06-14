package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.AllergenDTO;
import com.example.entity.Allergen;
import com.example.mapper.AllergenMapper;
import com.example.repository.AllergenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AllergenService {

	private final AllergenRepository allergenRepository;
	private final AllergenMapper allergenMapper;

	public List<AllergenDTO> getAllAllergens() {
		return allergenRepository.findAll().stream().map(allergenMapper::toDTO).collect(Collectors.toList());
	}

	public AllergenDTO getAllergenById(Long id) {
		Allergen allergen = allergenRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Alérgeno no encontrado con id: " + id));
		return allergenMapper.toDTO(allergen);
	}

	public AllergenDTO createAllergen(AllergenDTO allergenDTO) {
		Allergen allergen = allergenMapper.toEntity(allergenDTO);
		Allergen saved = allergenRepository.save(allergen);
		return allergenMapper.toDTO(saved);
	}

	public AllergenDTO updateAllergen(Long id, AllergenDTO allergenDTO) {
		Allergen existing = allergenRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Alérgeno no encontrado con id: " + id));
		existing.setName(allergenDTO.getName());
		existing.setIconUrl(allergenDTO.getIconUrl());
		Allergen updated = allergenRepository.save(existing);
		return allergenMapper.toDTO(updated);
	}

	public void deleteAllergen(Long id) {
		if (!allergenRepository.existsById(id)) {
			throw new RuntimeException("Alérgeno no encontrado con id: " + id);
		}
		allergenRepository.deleteById(id);
	}
}
