package com.example.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dto.UserAccountDTO;
import com.example.entity.UserAccount;
import com.example.enums.UserRole;
import com.example.mapper.UserAccountMapper;
import com.example.repository.UserAccountRepository;
import com.example.security.JwtTokenProvider;
import com.example.util.ValidationUtil;
import com.marketplace.exception.BusinessException;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAccountService {

	private final UserAccountRepository userAccountRepository;
	private final UserAccountMapper userMapper;
	private final ValidationUtil validationUtil;
	@Autowired
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final EmailService emailService;

	public UserAccountDTO getUserProfile(String email) {
		return userAccountRepository.findByEmail(email).map(userMapper::toDTO)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
	}

	public Map<String, Object> updateUserProfileWithTokenCheck(String currentEmail, UserAccountDTO updatedUser) {
		UserAccount user = userAccountRepository.findByEmail(currentEmail)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		String newEmail = updatedUser.getEmail();
		boolean emailChanged = newEmail != null && !currentEmail.equals(newEmail);

		if (emailChanged) {
			if (userAccountRepository.existsByEmail(newEmail)) {
				throw new RuntimeException("El nuevo email ya está registrado por otro usuario");
			}
			if (!validationUtil.isValidEmail(newEmail)) {
				throw new RuntimeException("El nuevo email no tiene un formato válido");
			}
		}

		boolean wantsToChangePassword = updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank();

		if (wantsToChangePassword) {
			if (updatedUser.getOldPassword() == null || updatedUser.getOldPassword().isBlank()) {
				throw new RuntimeException("Debe proporcionar la contraseña actual para cambiarla.");
			}
			if (!passwordEncoder.matches(updatedUser.getOldPassword(), user.getPassword())) {
				throw new RuntimeException("La contraseña actual no es correcta.");
			}
			user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
		}

		if (updatedUser.getName() != null)
			user.setName(updatedUser.getName());

		if (updatedUser.getSurname() != null) {
			user.setSurname(updatedUser.getSurname());
		} else if (user.getSurname() == null) {
			user.setSurname("");
		}

		if (updatedUser.getEmail() != null)
			user.setEmail(updatedUser.getEmail());
		if (updatedUser.getPhoneNumber() != null)
			user.setPhoneNumber(updatedUser.getPhoneNumber());
		if (updatedUser.getProfileImage() != null)
			user.setProfileImage(updatedUser.getProfileImage());

		UserAccount savedUser = userAccountRepository.save(user);

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Actualizado correctamente");

		if (emailChanged) {
			String newToken = jwtTokenProvider.generateToken(savedUser);
			UserAccountDTO updatedUserDTO = userMapper.toDTO(savedUser);

			response.put("newToken", newToken);
			response.put("user", updatedUserDTO);
			response.put("emailChanged", true);

			System.out.println("🔄 Email changed from " + currentEmail + " to " + newEmail + ". New token generated.");
		}

		return response;
	}

	public Map<String, Object> registerUser(UserAccountDTO userDTO) {
		List<String> errors = new ArrayList<>();
		UserRole roleEnum;
		try {
			roleEnum = userDTO.getRole() == null ? UserRole.CLIENT : UserRole.valueOf(userDTO.getRole().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(List.of("Rol inválido: debe ser CLIENT o ADMIN"));
		}

		if (userAccountRepository.existsByEmail(userDTO.getEmail())) {
			errors.add("El email ya está registrado.");
		}

		if (!validationUtil.isValidEmail(userDTO.getEmail())) {
			errors.add("El email no tiene un formato válido.");
		}

		if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().trim().isBlank()
				&& !validationUtil.isValidPhone(userDTO.getPhoneNumber())) {
			errors.add("El teléfono debe tener 9 dígitos.");
		}

		if (!validationUtil.isValidPassword(userDTO.getPassword())) {
			errors.add(
					"La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y un carácter especial.");
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		UserAccount user = userMapper.toEntity(userDTO);
		user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encripta la contraseña
		user.setRole(roleEnum);
		UserAccount savedUser = userAccountRepository.save(user);
		try {
			String html = emailService.buildEmailHtml(savedUser.getName(), "¡Bienvenido a Panadería Ana!",
					"Gracias por registrarte en nuestra web. Nos alegra tenerte con nosotros.", """
							<p style="margin-top: 15px;">
							  ¡Anímate a probar la gran selección de productos artesanales que tenemos actualmente!<br/>
							  Panes, dulces y más, hechos con amor cada día.
							</p>
							""");

			emailService.sendOrderEmail(savedUser.getEmail(), "Bienvenido a Panadería Ana", html);
		} catch (MessagingException e) {
			System.err.println("❌ No se pudo enviar el email de bienvenida: " + e.getMessage());
		}

		String token = jwtTokenProvider.generateToken(savedUser);

		Map<String, Object> response = new HashMap<>();
		response.put("user", userMapper.toDTO(savedUser));
		response.put("token", token);
		return response;
	}

	public void updateUserProfile(String email, UserAccountDTO updatedUser) {
		UserAccount user = userAccountRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		boolean wantsToChangePassword = updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank();

		if (wantsToChangePassword) {
			if (updatedUser.getOldPassword() == null || updatedUser.getOldPassword().isBlank()) {
				throw new RuntimeException("Debe proporcionar la contraseña actual para cambiarla.");
			}
			if (!passwordEncoder.matches(updatedUser.getOldPassword(), user.getPassword())) {
				throw new RuntimeException("La contraseña actual no es correcta.");
			}
			user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
		}

		if (updatedUser.getName() != null)
			user.setName(updatedUser.getName());

		if (updatedUser.getSurname() != null) {
			user.setSurname(updatedUser.getSurname());
		} else if (user.getSurname() == null) {
			user.setSurname("");
		}

		if (updatedUser.getEmail() != null)
			user.setEmail(updatedUser.getEmail());
		if (updatedUser.getPhoneNumber() != null)
			user.setPhoneNumber(updatedUser.getPhoneNumber());
		if (updatedUser.getProfileImage() != null)
			user.setProfileImage(updatedUser.getProfileImage());

		userAccountRepository.save(user);
	}

	public void generateResetTokenAndSendEmail(String email) {
		UserAccount user = userAccountRepository.findByEmail(email)
				.orElseThrow(() -> new BusinessException("No existe usuario con ese email"));
		String token = UUID.randomUUID().toString();
		user.setResetToken(token);
		user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
		userAccountRepository.save(user);

		String resetLink = "http://localhost:3000/reset-password?token=" + token;
		String html = "<p>Hola, " + user.getName()
				+ ".<br>Has solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para continuar:</p>"
				+ "<a href='" + resetLink + "'>Restablecer contraseña</a>"
				+ "<p>Si no solicitaste este cambio, ignora este correo.</p>";
		try {
			emailService.sendOrderEmail(user.getEmail(), "Recuperación de contraseña - Panadería Ana", html);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("No se pudo enviar el email de recuperación");
		}
	}

	public void resetPassword(String token, String newPassword) {
		UserAccount user = userAccountRepository.findByResetToken(token)
				.orElseThrow(() -> new BusinessException("Token inválido"));
		if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
			throw new BusinessException("El token ha expirado");
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setResetToken(null);
		user.setResetTokenExpiry(null);
		userAccountRepository.save(user);
	}

}