package com.marketplace.exception.handler;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.marketplace.exception.ApiError;
import com.marketplace.exception.BusinessException;
import com.marketplace.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
		ApiError error = new ApiError();
		error.setStatus(HttpStatus.NOT_FOUND);
		error.setDate(LocalDateTime.now());
		error.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("timestamp", LocalDateTime.now(), "status",
				HttpStatus.BAD_REQUEST.value(), "errors", ex.getErrors()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGenericException(Exception ex) {
		ApiError error = new ApiError();
		error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		error.setDate(LocalDateTime.now());
		error.setMessage("Ha ocurrido un error inesperado. Contacte al soporte.");
		ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
