package com.example.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.BakeryInfoDTO;
import com.example.service.BakeryInfoService;
import com.example.service.EmailService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class BakeryInfoController {
	private final EmailService emailService;
	private final BakeryInfoService bakeryInfoService;

	@GetMapping
	public ResponseEntity<BakeryInfoDTO> getBakeryInfo() {
		return ResponseEntity.ok(bakeryInfoService.getBakeryInfo());
	}

	@PostMapping
	public ResponseEntity<BakeryInfoDTO> create(@RequestBody BakeryInfoDTO dto) {
		return ResponseEntity.ok(bakeryInfoService.createBakeryInfo(dto));
	}

	@PostMapping("/contact")
	public void enviarMensaje(@RequestBody Map<String, String> payload) throws MessagingException {
		String nombre = payload.get("name");
		String correo = payload.get("email");
		String mensaje = payload.get("message");

		String contenidoHtml = emailService.buildSimpleContactHtml(nombre, correo, mensaje);
		String destinatario = bakeryInfoService.getBakeryInfo().getEmail();

		emailService.sendContactEmail(destinatario, "Consulta nueva en la web", contenidoHtml, correo);
	}

	@PutMapping("/{id}")
	public ResponseEntity<BakeryInfoDTO> update(@PathVariable Long id, @RequestBody BakeryInfoDTO dto) {
		return ResponseEntity.ok(bakeryInfoService.updateBakeryInfo(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		bakeryInfoService.deleteBakeryInfo(id);
		return ResponseEntity.noContent().build();
	}

}