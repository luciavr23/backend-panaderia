package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.FoodOrderDTO;
import com.example.dto.FoodOrderPaymentDTO;
import com.example.dto.OrderedItemDTO;
import com.example.dto.ReviewDTO;
import com.example.entity.BakeryInfo;
import com.example.entity.FoodOrder;
import com.example.entity.OrderProduct;
import com.example.entity.Product;
import com.example.entity.UserAccount;
import com.example.enums.OrderStatusEnum;
import com.example.mapper.FoodOrderMapper;
import com.example.mapper.ReviewMapper;
import com.example.repository.BakeryInfoRepository;
import com.example.repository.FoodOrderRepository;
import com.example.repository.OrderProductRepository;
import com.example.repository.ProductRepository;
import com.example.repository.ReviewRepository;
import com.example.repository.UserAccountRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodOrderService {
	private final FoodOrderRepository foodOrderRepository;
	private final UserAccountRepository userAccountRepository;
	private final BakeryInfoRepository bakeryInfoRepository;
	@Autowired
	private EmailService emailService;
	private final ReviewRepository reviewRepository;
	private final ReviewMapper reviewMapper;
	private final FoodOrderMapper foodOrderMapper;
	private final OrderProductRepository orderProductRepository;
	private final ProductRepository productRepository;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Value("${stripe.secret-key}")
	private String secretKey;

	public List<FoodOrderDTO> getOrdersByUsername(String email) {
		UserAccount user = userAccountRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		return getOrdersByUserId(user.getId());
	}

	public FoodOrderDTO findById(Long id) {
		FoodOrder order = foodOrderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
		return foodOrderMapper.toDto(order);
	}

	public void cancelarPagoStripe(String paymentIntentId) throws Exception {
		Stripe.apiKey = secretKey;
		PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
		intent.cancel();
	}

	public List<FoodOrderDTO> getOrdersByUserId(Long userId) {
		List<FoodOrder> orders = foodOrderRepository.findByUserId(userId);
		return orders.stream().map(this::toDtoWithReview).collect(Collectors.toList());
	}

	private FoodOrderDTO toDtoWithReview(FoodOrder order) {
		FoodOrderDTO dto = foodOrderMapper.toDto(order);
		ReviewDTO reviewDTO = reviewRepository.findByOrderId(order.getId()).map(reviewMapper::toDTO).orElse(null);
		dto.setReview(reviewDTO);
		return dto;
	}

	public FoodOrder crearPedido(FoodOrder pedido) {
		pedido.setOrderNumber(null);
		pedido.setCreatedAt(LocalDateTime.now());
		FoodOrder saved = foodOrderRepository.save(pedido);

		String numeroPedido = String.format("PED-%04d", saved.getId());
		saved.setOrderNumber(numeroPedido);

		return foodOrderRepository.save(saved);
	}

	public void actualizarEstadoPedido(Long orderId, OrderStatusEnum nuevoEstado) {
		FoodOrder pedido = foodOrderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

		if (pedido.getStatus() == OrderStatusEnum.LISTO || pedido.getStatus() == OrderStatusEnum.CANCELADO) {
			throw new RuntimeException("El pedido ya fue finalizado o cancelado");
		}

		pedido.setStatus(nuevoEstado);

		if (nuevoEstado == OrderStatusEnum.LISTO || nuevoEstado == OrderStatusEnum.CANCELADO) {
			pedido.setEndedAt(LocalDateTime.now());
		}

		if (nuevoEstado == OrderStatusEnum.CANCELADO) {
			for (OrderProduct item : pedido.getItems()) {
				Product producto = item.getProduct();
				producto.setStock(producto.getStock() + item.getQuantity());
			}
		}

		foodOrderRepository.save(pedido);

		switch (nuevoEstado) {
		case PENDIENTE -> notificarPedidoRegistrado(pedido);
		case LISTO -> notificarPedidoCompletado(pedido);
		case CANCELADO -> notificarPedidoCancelado(pedido);
		default -> {
		}
		}
	}

	@Transactional
	public FoodOrderDTO createOrderWithStockControl(FoodOrderPaymentDTO dto, String userEmail) {
		System.out.println("📩 Email recibido: " + userEmail);
		System.out.println("🛒 DTO recibido: " + dto);

		UserAccount user = userAccountRepository.findByEmail(userEmail).orElseThrow(() -> {
			System.out.println("❌ Usuario no encontrado: " + userEmail);
			return new RuntimeException("Usuario no encontrado");
		});

		System.out.println("✅ Usuario autenticado encontrado: " + user.getEmail());

		FoodOrder order = FoodOrder.builder().user(user).createdAt(LocalDateTime.now())
				.status(OrderStatusEnum.EN_PREPARACION).build();

		List<OrderProduct> orderItems = new ArrayList<>();
		BigDecimal total = BigDecimal.ZERO;

		for (OrderedItemDTO itemDto : dto.getProducts()) {
			System.out.println(
					"🔍 Procesando producto ID " + itemDto.getProductId() + " cantidad " + itemDto.getQuantity());

			Product product = productRepository.findById(itemDto.getProductId()).orElseThrow(() -> {
				System.out.println("❌ Producto no encontrado con ID " + itemDto.getProductId());
				return new RuntimeException("Producto no encontrado con ID: " + itemDto.getProductId());
			});

			if (product.getStock() < itemDto.getQuantity()) {
				System.out.println("⚠️ Sin stock suficiente para producto " + product.getName());
				throw new RuntimeException("No hay suficiente stock para el producto: " + product.getName());
			}

			BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
			total = total.add(lineTotal);

			product.setStock(product.getStock() - itemDto.getQuantity());
			if (product.getStock() <= 0) {
				product.setAvailable(false);
			}
			productRepository.save(product);

			OrderProduct orderProduct = OrderProduct.builder().product(product).quantity(itemDto.getQuantity())
					.unitPrice(product.getPrice()).order(order).build();

			orderItems.add(orderProduct);
		}

		order.setItems(orderItems);
		order.setTotalPrice(total);

		System.out.println("💾 Guardando pedido... Total: " + total);

		FoodOrder saved = foodOrderRepository.save(order);
		saved.setOrderNumber(String.format("ORD-%04d", saved.getId()));
		saved = foodOrderRepository.save(saved);

		messagingTemplate.convertAndSend("/topic/new-order", foodOrderMapper.toDto(saved));

		notificarPedidoRegistrado(saved);

		return foodOrderMapper.toDto(saved);
	}

	public void notificarPedidoRegistrado(FoodOrder pedido) {
		try {
			UserAccount cliente = pedido.getUser();

			if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
				System.out.println("❌ Pedido sin usuario o email inválido.");
				return;
			}

			List<String> productos = pedido.getItems().stream().map(item -> {
				if (item.getProduct() == null) {
					return "Producto desconocido x" + item.getQuantity();
				}
				return item.getProduct().getName() + " x" + item.getQuantity();
			}).toList();

			String resumen = emailService.buildResumenPedidoHtml(pedido.getId().toString(), productos,
					pedido.getTotalPrice().doubleValue(), null);

			String mensaje = "Se ha registrado tu pedido y pasa a estar en preparación. ¡Gracias por confiar en Panadería Ana!";
			String html = emailService.buildEmailHtml(cliente.getName() != null ? cliente.getName() : "Cliente",
					"¡Tu pedido ha sido registrado!", mensaje, resumen);

			emailService.sendOrderEmail(cliente.getEmail(), "¡Tu pedido ha sido registrado!", html);
			System.out.println("✅ Email enviado correctamente a " + cliente.getEmail());

		} catch (Exception e) {
			System.out.println("❌ Error al enviar correo de pedido registrado: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void notificarPedidoCompletado(FoodOrder pedido) {
		try {
			UserAccount cliente = pedido.getUser();
			if (cliente == null || cliente.getEmail() == null)
				return;

			List<String> productos = pedido.getItems().stream()
					.map(item -> item.getProduct().getName() + " x" + item.getQuantity()).toList();

			String resumen = emailService.buildResumenPedidoHtml(pedido.getId().toString(), productos,
					pedido.getTotalPrice().doubleValue(), null // sin fecha de recogida
			);

			BakeryInfo info = bakeryInfoRepository.findAll().stream().findFirst().orElse(null);
			String direccion = info != null
					? info.getStreet() + ", " + info.getMunicipality() + ", " + info.getCity() + ", "
							+ info.getPostalCode() + ", " + info.getCountry()
					: "";
			String telefono = info != null ? info.getPhone() : "";

			String mensaje = "Tu pedido ya está listo para recoger en tienda desde este momento.";
			String html = emailService.buildEmailHtml(cliente.getName(), "Tu pedido está listo para recoger", mensaje,
					resumen + "<p><b>Dirección:</b> " + direccion + "<br/><b>Teléfono:</b> " + telefono + "</p>");

			emailService.sendOrderEmail(cliente.getEmail(), "¡Tu pedido está listo para recoger!", html);

		} catch (Exception e) {
			System.out.println("❌ Error al enviar correo de pedido completado: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void notificarPedidoCancelado(FoodOrder pedido) {
		try {
			UserAccount cliente = pedido.getUser();
			if (cliente == null || cliente.getEmail() == null)
				return;

			List<String> productos = pedido.getItems().stream()
					.map(item -> item.getProduct().getName() + " x" + item.getQuantity()).toList();

			String resumen = emailService.buildResumenPedidoHtml(pedido.getId().toString(), productos,
					pedido.getTotalPrice().doubleValue(), null);

			String mensaje = "Lamentamos las molestias, tu pedido ha sido cancelado.";
			String html = emailService.buildEmailHtml(cliente.getName(), "Pedido cancelado", mensaje, resumen);

			emailService.sendOrderEmail(cliente.getEmail(), "Pedido cancelado", html);

		} catch (Exception e) {
			System.out.println("❌ Error al enviar correo de pedido cancelado: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void enviarTicketPorEmail(FoodOrder pedido) {
		try {
			UserAccount cliente = pedido.getUser();

			if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
				System.out.println("❌ Pedido sin usuario o email inválido.");
				return;
			}

			List<String> productos = pedido.getItems().stream().map(item -> {
				if (item.getProduct() == null) {
					return "Producto desconocido x" + item.getQuantity();
				}
				return item.getProduct().getName() + " x" + item.getQuantity();
			}).toList();

			String resumen = emailService.buildResumenPedidoHtml(pedido.getId().toString(), productos,
					pedido.getTotalPrice().doubleValue(), null);

			String mensaje = "Aquí tienes tu ticket, " + cliente.getEmail() + ". Resumen del pedido:";
			String html = emailService.buildEmailHtml(cliente.getName() != null ? cliente.getName() : "Cliente",
					"🎟 Tu ticket de pedido", mensaje, resumen);

			emailService.sendOrderEmail(cliente.getEmail(), "Tu ticket de pedido", html);
			System.out.println("✅ Ticket reenviado correctamente a " + cliente.getEmail());

		} catch (Exception e) {
			System.out.println("❌ Error al reenviar ticket: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void reenviarTicketPedido(Long orderId, String userEmail) {
		FoodOrder pedido = foodOrderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

		if (!pedido.getUser().getEmail().equals(userEmail)) {
			throw new RuntimeException("No tienes permiso para este pedido");
		}

		enviarTicketPorEmail(pedido);
	}

	@Scheduled(cron = "0 0 * * * *")
	public void cancelarPedidosPendientes() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);

		List<FoodOrder> pedidosPendientes = foodOrderRepository.findByCreatedAtBetweenAndStatus(startOfDay, endOfDay,
				OrderStatusEnum.EN_PREPARACION);

		for (FoodOrder pedido : pedidosPendientes) {
			pedido.setStatus(OrderStatusEnum.CANCELADO);
			pedido.setEndedAt(LocalDateTime.now());
			foodOrderRepository.save(pedido);
			notificarPedidoCancelado(pedido);

			messagingTemplate.convertAndSend("/topic/order-cancelled", foodOrderMapper.toDto(pedido));
		}
	}

	public List<FoodOrderDTO> getPedidosDelDia() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);

		List<FoodOrder> pedidos = foodOrderRepository.findByCreatedAtBetween(startOfDay, endOfDay);
		return pedidos.stream().map(foodOrderMapper::toDto).collect(Collectors.toList());
	}

}
