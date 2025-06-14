package com.example.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dto.DailySpecialDTO;
import com.example.entity.DailySpecial;
import com.example.entity.Product;
import com.example.enums.WeekdayEnum;
import com.example.mapper.DailySpecialMapper;
import com.example.repository.DailySpecialRepository;
import com.example.repository.ProductRepository;
import com.marketplace.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailySpecialService {

	private final DailySpecialRepository dailySpecialRepository;
	private final ProductRepository productRepository;
	private final DailySpecialMapper dailySpecialMapper;

	public List<DailySpecialDTO> getAll() {
		return dailySpecialRepository.findAll().stream().map(dailySpecialMapper::toDTO).toList();
	}

	public DailySpecialDTO getByWeekday(WeekdayEnum weekday) {
		DailySpecial special = dailySpecialRepository.findByWeekday(weekday)
				.orElseThrow(() -> new BusinessException("No hay plato del día para " + weekday));
		return dailySpecialMapper.toDTO(special);
	}

	public DailySpecialDTO create(DailySpecialDTO dto) {
		if (dailySpecialRepository.findByWeekday(dto.getWeekday()).isPresent()) {
			throw new BusinessException("Ya existe un plato del día para " + dto.getWeekday());
		}

		Product product = productRepository.findById(dto.getProductId())
				.orElseThrow(() -> new BusinessException("Producto no encontrado con ID: " + dto.getProductId()));

		DailySpecial newSpecial = DailySpecial.builder().weekday(dto.getWeekday()).product(product).build();

		DailySpecial saved = dailySpecialRepository.save(newSpecial);
		return dailySpecialMapper.toDTO(saved);
	}

	public DailySpecialDTO update(Long id, DailySpecialDTO dto) {
		DailySpecial existing = dailySpecialRepository.findById(id)
				.orElseThrow(() -> new BusinessException("No existe un plato del día con ID: " + id));

		if (dto.getWeekday() != null)
			existing.setWeekday(dto.getWeekday());

		if (dto.getProductId() != null) {
			Product product = productRepository.findById(dto.getProductId())
					.orElseThrow(() -> new BusinessException("Producto no encontrado con ID: " + dto.getProductId()));
			existing.setProduct(product);
		}

		DailySpecial updated = dailySpecialRepository.save(existing);
		return dailySpecialMapper.toDTO(updated);
	}

	public void delete(Long id) {
		if (!dailySpecialRepository.existsById(id)) {
			throw new BusinessException("No existe un plato del día con ID: " + id);
		}
		dailySpecialRepository.deleteById(id);
	}
}
