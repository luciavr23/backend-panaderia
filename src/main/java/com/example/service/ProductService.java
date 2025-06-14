package com.example.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.dto.AllergenDTO;
import com.example.dto.OrderProductDTO;
import com.example.dto.ProductDTO;
import com.example.dto.ProductImageDTO;
import com.example.entity.Allergen;
import com.example.entity.Product;
import com.example.entity.ProductAllergen;
import com.example.entity.ProductCategory;
import com.example.entity.ProductImage;
import com.example.enums.WeekdayEnum;
import com.example.mapper.ProductMapper;
import com.example.repository.AllergenRepository;
import com.example.repository.ProductAllergenRepository;
import com.example.repository.ProductRepository;
import com.marketplace.exception.InsufficientStockException;
import com.marketplace.exception.ProductNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;
	@Autowired
	private ProductAllergenRepository productAllergenRepository;
	@Autowired
	private AllergenRepository allergenRepository;
	private final SimpMessagingTemplate messagingTemplate;

	public List<ProductDTO> getAllProductsWithoutCategory() {
		return productRepository.findByAvailableTrue().stream().map(productMapper::toDTO).collect(Collectors.toList());
	}

	public void validateStock(List<OrderProductDTO> items) {
		for (OrderProductDTO item : items) {
			Product product = productRepository.findById(item.getId())
					.orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con ID " + item.getId()));

			if (product.getStock() < item.getQuantity()) {
				throw new InsufficientStockException("No hay suficiente stock para " + product.getName());
			}
		}
	}

	public Page<ProductDTO> getAvailableProducts(int page, int size, String sortBy, String direction, String search,
			Long categoryId) {
		Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

		WeekdayEnum today = switch (LocalDate.now().getDayOfWeek()) {
		case MONDAY -> WeekdayEnum.LUNES;
		case TUESDAY -> WeekdayEnum.MARTES;
		case WEDNESDAY -> WeekdayEnum.MIÉRCOLES;
		case THURSDAY -> WeekdayEnum.JUEVES;
		case FRIDAY -> WeekdayEnum.VIERNES;
		case SATURDAY -> WeekdayEnum.SÁBADO;
		case SUNDAY -> WeekdayEnum.DOMINGO;
		};

		Page<Product> products;

		if (categoryId != null) {
			if (search != null && !search.isEmpty()) {
				products = productRepository.findAvailableForTodayWithCategoryAndSearch(categoryId, search, today,
						pageable);
			} else {
				products = productRepository.findAvailableForTodayWithCategory(categoryId, today, pageable);
			}
		} else {
			if (search != null && !search.isEmpty()) {
				products = productRepository.findAvailableForTodayWithSearch(today, search, pageable);
			} else {
				products = productRepository.findAvailableForToday(today, pageable);
			}
		}

		return products.map(productMapper::toDTO);
	}

	public Page<ProductDTO> getAllProducts(int page, int size, String sortBy, String direction, String search,
			Long categoryId) {
		Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

		Page<Product> products;

		if (categoryId != null) {
			if (search != null && !search.isEmpty()) {
				products = productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, search, pageable);
			} else {
				products = productRepository.findByCategoryId(categoryId, pageable);
			}
		} else {
			if (search != null && !search.isEmpty()) {
				products = productRepository.findByNameContainingIgnoreCase(search, pageable);
			} else {
				products = productRepository.findAll(pageable);
			}
		}

		return products.map(productMapper::toDTO);
	}

	public ProductDTO getProductById(Long id) {
		return productRepository.findById(id).map(productMapper::toDTO)
				.orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
	}

	public List<ProductDTO> getPopularProducts() {
		return productRepository.findByPopularTrue().stream().map(productMapper::toDTO).collect(Collectors.toList());
	}

	public ProductDTO createProduct(ProductDTO dto) {
		Product entity = productMapper.toEntity(dto);

		if (entity.getImages() != null) {
			for (ProductImage image : entity.getImages()) {
				image.setProduct(entity);
			}
		}

		if (entity.getAllergens() != null) {
			entity.getAllergens().clear();
		} else {
			entity.setAllergens(new ArrayList<>());
		}

		if (dto.getAllergens() != null) {
			for (AllergenDTO allergenDTO : dto.getAllergens()) {
				Allergen allergen = allergenRepository.getReferenceById(allergenDTO.getId());
				ProductAllergen pa = new ProductAllergen();
				pa.setProduct(entity);
				pa.setAllergen(allergen);
				entity.getAllergens().add(pa);
			}
		}

		Product saved = productRepository.save(entity);
		return productMapper.toDTO(saved);
	}

	private void synchronizeImages(Product product, List<ProductImageDTO> imageDTOs) {

		Map<Long, ProductImageDTO> dtoMap = imageDTOs.stream().filter(dto -> dto.getId() != null)
				.collect(Collectors.toMap(ProductImageDTO::getId, dto -> dto));

		List<ProductImage> toRemove = product.getImages().stream().filter(img -> !dtoMap.containsKey(img.getId()))
				.toList();

		product.getImages().removeAll(toRemove);

		for (ProductImage img : product.getImages()) {
			ProductImageDTO dto = dtoMap.get(img.getId());
			if (dto != null) {
				img.setImageUrl(dto.getImageUrl());
				img.setOrder(dto.getOrder());
			}
		}

		List<ProductImage> newImages = imageDTOs.stream().filter(dto -> dto.getId() == null).map(dto -> ProductImage
				.builder().imageUrl(dto.getImageUrl()).order(dto.getOrder()).product(product).build()).toList();

		product.getImages().addAll(newImages);
	}

	public ProductDTO updateProduct(Long id, ProductDTO dto) {
		Product existing = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

		if (dto.getName() != null)
			existing.setName(dto.getName());

		if (dto.getDescription() != null)
			existing.setDescription(dto.getDescription());

		if (dto.getPrice() != null)
			existing.setPrice(dto.getPrice());

		if (dto.getStock() != null) {
			existing.setStock(dto.getStock());
			if (dto.getStock() > 0 && (existing.getAvailable() == null || !existing.getAvailable())) {
				existing.setAvailable(true);
			}
		}

		if (dto.getAvailable() != null) {
			existing.setAvailable(dto.getAvailable());
		}

		if (dto.getPopular() != null) {
			existing.setPopular(dto.getPopular());
		}
		if (dto.getAllergens() != null) {
			List<Long> nuevosIds = dto.getAllergens().stream().map(AllergenDTO::getId).collect(Collectors.toList());

			List<ProductAllergen> existentes = productAllergenRepository.findByProductId(existing.getId());
			for (ProductAllergen pa : existentes) {
				if (!nuevosIds.contains(pa.getAllergen().getId())) {
					productAllergenRepository.delete(pa);
				}
			}

			for (Long idAlergeno : nuevosIds) {
				boolean yaExiste = existentes.stream().anyMatch(pa -> pa.getAllergen().getId().equals(idAlergeno));
				if (!yaExiste) {
					Allergen alergeno = allergenRepository.findById(idAlergeno)
							.orElseThrow(() -> new RuntimeException("Alérgeno no encontrado: " + idAlergeno));

					ProductAllergen nueva = new ProductAllergen();
					nueva.setProduct(existing);
					nueva.setAllergen(alergeno);
					productAllergenRepository.save(nueva);
				}
			}
		}

		if (dto.getCategoryId() != null) {
			ProductCategory category = new ProductCategory();
			category.setId(dto.getCategoryId());
			existing.setCategory(category);
		}

		if (dto.getImages() != null && !dto.getImages().isEmpty()) {
			if (existing.getImages() == null) {
				existing.setImages(
						dto.getImages().stream()
								.map(imgDto -> ProductImage.builder().imageUrl(imgDto.getImageUrl())
										.order(imgDto.getOrder()).product(existing).build())
								.collect(Collectors.toList()));
			} else {
				synchronizeImages(existing, dto.getImages());
			}
		}

		Product updated = productRepository.save(existing);
		messagingTemplate.convertAndSend("/topic/products", productMapper.toDTO(updated));

		return productMapper.toDTO(updated);
	}

	public void deleteProduct(Long id) {
		if (!productRepository.existsById(id)) {
			throw new RuntimeException("Producto no encontrado con ID: " + id);
		}
		productRepository.deleteById(id);
	}
}
