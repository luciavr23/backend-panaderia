package com.marketplace.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BusinessException extends RuntimeException {

	private final List<String> errors;

	public BusinessException(String message) {
		super(message);
		this.errors = Collections.singletonList(message);
	}

	public BusinessException(List<String> messages) {
		super(String.join("; ", messages));
		this.errors = new ArrayList<>(messages);
	}

	public List<String> getErrors() {
		return errors;
	}
}
