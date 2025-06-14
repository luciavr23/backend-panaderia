package com.example.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.FoodOrderDTO;
import com.example.dto.FoodOrderPaymentDTO;
import com.example.entity.FoodOrder;
import com.example.enums.OrderStatusEnum;
import com.example.repository.FoodOrderRepository;
import com.example.service.FoodOrderService;
import com.example.service.StripeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class FoodOrderController {
	private final FoodOrderService foodOrderService;
	private final FoodOrderRepository foodOrderRepository;
	private final StripeService stripeService;

	@GetMapping("/my")
	public ResponseEntity<List<FoodOrderDTO>> getMyOrders(Authentication authentication) {
		String username = authentication.getName();
		List<FoodOrderDTO> orders = foodOrderService.getOrdersByUsername(username);
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<FoodOrderDTO>> getOrdersByUserId(@PathVariable Long userId) {
		List<FoodOrderDTO> orders = foodOrderService.getOrdersByUserId(userId);
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/{id}")
	public ResponseEntity<FoodOrderDTO> getOrderById(@PathVariable Long id) {
		return ResponseEntity.ok(foodOrderService.findById(id));
	}

	@PostMapping("/payment")
	public ResponseEntity<?> createOrder(@RequestBody FoodOrderPaymentDTO request, Authentication authentication) {
		System.out.println("📦 Productos recibidos: " + request.getProducts());
		System.out.println("💳 PaymentIntentId: " + request.getPaymentIntentId());

		String email = authentication.getName();
		System.out.println("🔐 Usuario autenticado: " + email);

		try {
			FoodOrderDTO dto = foodOrderService.createOrderWithStockControl(request, email);
			return ResponseEntity.ok(dto);
		} catch (RuntimeException e) {
			System.out.println("❌ Error al crear pedido: " + e.getMessage());

			if (request.getPaymentIntentId() != null) {
				try {
					stripeService.cancelPayment(request.getPaymentIntentId());
					System.out.println("🔁 Se canceló el pago Stripe: " + request.getPaymentIntentId());
				} catch (Exception ex) {
					System.out.println("❌ Error cancelando pago: " + ex.getMessage());
				}
			}

			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	@PutMapping("/{orderId}/status")
	public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
		try {
			foodOrderService.actualizarEstadoPedido(orderId, OrderStatusEnum.valueOf(status));
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", "Estado no válido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
		}
	}

	@PostMapping("/{orderId}/resend-ticket")
	public ResponseEntity<?> resendTicket(@PathVariable Long orderId, Authentication authentication) {
		String email = authentication.getName();
		try {
			foodOrderService.reenviarTicketPedido(orderId, email);
			return ResponseEntity.ok(Map.of("message", "El ticket ha sido reenviado al correo"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(403).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteOrder(@PathVariable Long id, Authentication auth) {
		String email = auth.getName();

		FoodOrder order = foodOrderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

		if (!order.getUser().getEmail().equals(email)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (!order.getStatus().equals(OrderStatusEnum.CANCELADO)) {
			return ResponseEntity.badRequest().body(Map.of("message", "Solo se pueden eliminar pedidos cancelados"));
		}

		foodOrderRepository.delete(order);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/today")
	public ResponseEntity<List<FoodOrderDTO>> getPedidosDelDia() {
		return ResponseEntity.ok(foodOrderService.getPedidosDelDia());
	}

}