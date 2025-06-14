
package com.example.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.dto.AllergenDTO;
import com.example.dto.ProductDTO;
import com.example.dto.ProductImageDTO;
import com.example.entity.Product;
import com.example.entity.ProductAllergen;
import com.example.entity.ProductImage;

@Mapper(componentModel = "spring", uses = { ProductImageMapper.class })
public interface ProductMapper {

	@Mapping(source = "category.id", target = "categoryId")
	@Mapping(source = "images", target = "imageUrl", qualifiedByName = "mapFirstImageUrl")
	@Mapping(source = "allergens", target = "allergens", qualifiedByName = "mapAllergens")
	ProductDTO toDTO(Product product);

	@Mapping(source = "categoryId", target = "category.id")
	@Mapping(source = "images", target = "images")
	Product toEntity(ProductDTO dto);

	@Named("mapAllergens")
	default List<AllergenDTO> mapAllergens(List<ProductAllergen> productAllergens) {
		if (productAllergens == null)
			return null;
		return productAllergens.stream().map(pa -> new AllergenDTO(pa.getAllergen().getId(), pa.getAllergen().getName(),
				pa.getAllergen().getDescription(), pa.getAllergen().getIconUrl())).collect(Collectors.toList());

	}

	@Named("mapFirstImageUrl")
	default String mapFirstImageUrl(List<ProductImage> images) {
		if (images == null || images.isEmpty()) {
			return null;
		}

		return images.stream().filter(img -> img.getOrder() != null && img.getOrder() == 1).findFirst()
				.map(ProductImage::getImageUrl).orElse(images.get(0).getImageUrl());
	}

	default List<ProductImageDTO> mapImages(List<ProductImage> images) {
		if (images == null)
			return null;
		return images.stream().map(
				img -> new ProductImageDTO(img.getId(), img.getProduct().getId(), img.getImageUrl(), img.getOrder()))
				.collect(Collectors.toList());
	}
}
