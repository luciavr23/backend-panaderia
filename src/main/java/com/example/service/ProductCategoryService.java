package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.dto.ProductCategoryDTO;
import com.example.entity.ProductCategory;
import com.example.mapper.ProductCategoryMapper;
import com.example.repository.ProductCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

	private final ProductCategoryRepository categoryRepository;
	private final ProductCategoryMapper categoryMapper;

	public Page<ProductCategoryDTO> getAllCategories(Pageable pageable) {
		return categoryRepository.findAll(pageable).map(categoryMapper::toDTO);
	}

	public List<ProductCategoryDTO> getAll() {
		return categoryRepository.findAll().stream().map(categoryMapper::toDTO).collect(Collectors.toList());
	}

	public ProductCategoryDTO getCategoryById(Long id) {
		return categoryRepository.findById(id).map(categoryMapper::toDTO)
				.orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
	}

	public ProductCategoryDTO createCategory(ProductCategoryDTO dto) {
		var entity = categoryMapper.toEntity(dto);
		var saved = categoryRepository.save(entity);
		return categoryMapper.toDTO(saved);
	}

	public ProductCategoryDTO updateCategory(Long id, ProductCategoryDTO dto) {
		ProductCategory existing = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

		if (dto.getName() != null && !dto.getName().trim().isEmpty())
			existing.setName(dto.getName());

		if (dto.getDescription() != null && !dto.getDescription().trim().isEmpty())
			existing.setDescription(dto.getDescription());

		if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty())
			existing.setImageUrl(dto.getImageUrl());

		var updated = categoryRepository.save(existing);
		return categoryMapper.toDTO(updated);
	}

	public void deleteCategory(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new RuntimeException("Categoría no encontrada con ID: " + id);
		}
		categoryRepository.deleteById(id);
	}

}
