package com.marketplace.exception;

public class InsufficientStockException extends BusinessException {
	public InsufficientStockException(String message) {
		super(message);
	}
}
