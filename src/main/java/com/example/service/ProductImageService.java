package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.ProductImageDTO;
import com.example.entity.ProductImage;
import com.example.mapper.ProductImageMapper;
import com.example.repository.ProductImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageService {
	private final ProductImageRepository imageRepository;
	private final ProductImageMapper imageMapper;

	public List<ProductImageDTO> getImagesByProductId(Long productId) {
		return imageRepository.findByProductIdOrderByOrderAsc(productId).stream().map(imageMapper::toDTO)
				.collect(Collectors.toList());
	}

	public ProductImageDTO addImage(ProductImageDTO dto) {
		ProductImage entity = imageMapper.toEntity(dto);
		return imageMapper.toDTO(imageRepository.save(entity));
	}

	public void deleteImage(Long id) {
		imageRepository.deleteById(id);
	}

	public ProductImageDTO updateImageOrder(Long id, int order) {
		ProductImage image = imageRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Imagen no encontrada con ID: " + id));
		image.setOrder(order);
		return imageMapper.toDTO(imageRepository.save(image));
	}
}
