package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final StripeService stripeService;

	@PostMapping("/create-payment-intent")
	public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest request) {
		try {

			PaymentIntent intent = stripeService.createPaymentIntent(request.getAmount(), request.getCurrency(),
					request.getReceiptEmail());
			return ResponseEntity.ok(new PaymentResponse(intent.getClientSecret()));
		} catch (StripeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@Data
	public static class PaymentRequest {
		private Long amount;
		private String currency;
		private String receiptEmail;
	}

	@Data
	public static class PaymentResponse {
		private final String clientSecret;
	}
}