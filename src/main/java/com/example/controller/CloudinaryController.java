package com.example.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {

	@Value("${cloudinary.api.key}")
	private String apiKey;

	@Value("${cloudinary.api.secret}")
	private String apiSecret;

	@GetMapping("/signature")
	public ResponseEntity<Map<String, String>> generateSignature(@RequestParam String publicId) {
		long timestamp = System.currentTimeMillis() / 1000;

		String toSign = "public_id=" + publicId + "&timestamp=" + timestamp + "&upload_preset=panaderia" + apiSecret;
		String signature = DigestUtils.sha1Hex(toSign);

		Map<String, String> response = new HashMap<>();
		response.put("timestamp", String.valueOf(timestamp));
		response.put("signature", signature);
		response.put("apiKey", apiKey);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/delete/{publicId}")
	public ResponseEntity<String> deleteImage(@PathVariable String publicId) {
		try {
			Cloudinary cloudinary = new Cloudinary(
					Map.of("cloud_name", "dzcym3dh4", "api_key", apiKey, "api_secret", apiSecret));

			System.out.println("Intentando eliminar publicId: " + publicId);
			Map result = cloudinary.uploader().destroy(publicId, Map.of());
			System.out.println("Resultado Cloudinary: " + result);

			return ResponseEntity.ok("Imagen eliminada: " + result.get("result"));
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error al eliminar imagen: " + e.getMessage());
		}
	}
}
