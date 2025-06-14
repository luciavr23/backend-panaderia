package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeService {

	@Value("${stripe.secret-key}")
	private String secretKey;

	@PostConstruct
	public void init() {
		Stripe.apiKey = secretKey;
	}

	public void cancelPayment(String paymentIntentId) throws Exception {
		Stripe.apiKey = secretKey;
		PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
		intent.cancel();
	}

	public PaymentIntent createPaymentIntent(Long amount, String currency, String receiptEmail) throws StripeException {
		PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount(amount).setCurrency(currency)
				.setReceiptEmail(receiptEmail).build();

		return PaymentIntent.create(params);
	}

	public void verifyPaymentSuccess(String paymentIntentId) throws StripeException {
		PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
		if (!"succeeded".equals(intent.getStatus())) {
			throw new RuntimeException("El pago no se ha completado");
		}
	}

}
