package com.example.util;

import org.springframework.stereotype.Component;

import com.example.config.Constants;

@Component
public class ValidationUtil {

	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_]+(?:\\.[a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+(?:\\.[a-zA-Z0-9_]+)*(?:\\.(com|net|org|edu|es))$";
	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
	private static final String PHONE_REGEX = "^[0-9]{9}$";
	private static final String NIF_REGEX = "^[XYZ]?\\d{7,8}[A-Z]$|^[ABCDEFGHJNPQRSUVW]\\d{7}[0-9A-J]$";

	public boolean isValidEmail(String email) {
		return email != null && email.matches(EMAIL_REGEX);
	}

	public boolean isValidPassword(String password) {
		return password != null && password.matches(PASSWORD_REGEX);
	}

	public boolean isValidPhone(String phoneNumber) {
		return phoneNumber != null && phoneNumber.matches(PHONE_REGEX);
	}

	public boolean isValidNif(String nif) {
		return nif != null && nif.toUpperCase().matches(NIF_REGEX);
	}

	public boolean isValidRating(int rating) {
		return rating >= Constants.MIN_RATING && rating <= Constants.MAX_RATING;
	}
}
