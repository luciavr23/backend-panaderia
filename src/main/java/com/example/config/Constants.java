package com.example.config;

/**
 * Constants used throughout the application.
 */
public final class Constants {

	public static final int DEFAULT_PAGE_SIZE = 20;
	public static final int MAX_PAGE_SIZE = 100;

	public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
	public static final String[] ALLOWED_IMAGE_TYPES = { "image/jpeg", "image/png", "image/gif" };

	public static final int MIN_RATING = 1;
	public static final int MAX_RATING = 5;

	private Constants() {

	}
}
