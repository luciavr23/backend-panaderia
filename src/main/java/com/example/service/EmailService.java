package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendOrderEmail(String to, String subject, String htmlContent) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);

		mailSender.send(message);
	}

	public String buildEmailHtml(String nombreUsuario, String asunto, String mensajePrincipal, String contenidoHtml) {
		return """
				<!DOCTYPE html>
				<html lang="es">
				<head>
				  <meta charset="UTF-8">
				  <meta name="viewport" content="width=device-width, initial-scale=1.0">
				  <title>%s - Panadería Ana</title>
				</head>
				<body style="margin: 0; padding: 0; font-family: 'Segoe UI', sans-serif; background-color: #f5f5f5;">
				  <table width="100%%" cellpadding="0" cellspacing="0">
				    <tr>
				      <td align="center" style="padding: 30px 0;">
				        <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); overflow: hidden;">
				          <tr>
				            <td align="center" style="background-color: #E1E5C9; padding: 10px 0;">
				              <img src="https://res.cloudinary.com/dzcym3dh4/image/upload/logoPanaderia" alt="Panadería Ana" style="height: 190px;" />
				            </td>
				          </tr>
				          <tr>
				            <td style="padding: 30px;">
				              <h2 style="color: #333;">¡Hola, %s!</h2>
				              <p style="font-size: 16px; color: #555;">%s</p>
				              <div style="margin-top: 20px; font-size: 16px; color: #000;">
				                %s
				              </div>
				            </td>
				          </tr>
				          <tr>
				            <td align="center" style="padding: 20px; background-color: #f0f0f0; color: #777; font-size: 12px;">
				              Este es un mensaje automático. No respondas a este correo.
				            </td>
				          </tr>
				        </table>
				      </td>
				    </tr>
				  </table>
				</body>
				</html>
				"""
				.formatted(asunto, nombreUsuario, mensajePrincipal, contenidoHtml != null ? contenidoHtml : "");
	}

	public void sendContactEmail(String to, String subject, String htmlContent, String replyTo)
			throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);

		helper.setFrom("Panadería Ana <infopanaderiana@gmail.com>");

		if (replyTo != null && !replyTo.isBlank()) {
			helper.setReplyTo(replyTo);
		}

		mailSender.send(message);
	}

	public String buildSimpleContactHtml(String nombre, String correo, String mensaje) {
		return """
				<!DOCTYPE html>
				<html lang=\"es\">
				<head>
				  <meta charset=\"UTF-8\">
				  <title>Consulta nueva</title>
				</head>
				<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;\">
				  <div style=\"max-width: 600px; margin: auto; background-color: #fff; padding: 20px; border-radius: 8px;\">
				    <h2 style=\"color: #333;\">Tienes una consulta nueva de %s</h2>
				    <p style=\"font-size: 16px; color: #555;\"><b>Correo:</b> %s</p>
				    <p style=\"font-size: 16px; color: #555;\">Mensaje:</p>
				    <div style=\"background-color: #f9f9f9; padding: 15px; border-left: 4px solid #8DBB01;\">
				      <p style=\"margin: 0; color: #000;\">%s</p>
				    </div>
				  </div>
				</body>
				</html>
				"""
				.formatted(nombre, correo, mensaje);
	}

	public String buildOrderHtml(String cliente, String mensaje, String resumenPedido, String direccion,
			String telefono) {
		return """
				<html>
				  <body style='font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;'>
				    <div style='max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; overflow: hidden;'>
				      <div style='background-color: #E1E5C9; padding: 20px; text-align: center;'>
				        <img src='https://res.cloudinary.com/dzcym3dh4/image/upload/logoPanaderia' alt='Panadería Ana' style='width: 120px;' />
				      </div>
				      <div style='padding: 30px; text-align: left; color: #333;'>
				        <h2 style='color: #333;'>¡Hola, %s!</h2>
				        <p style='font-size: 16px;'>%s</p>
				        <div style='margin-top: 20px;'>%s</div>
				        %s
				      </div>
				      <div style='background-color: #f0f0f0; padding: 15px; text-align: center; font-size: 12px; color: #777;'>
				        Este es un mensaje automático. No respondas a este correo.
				      </div>
				    </div>
				  </body>
				</html>
				"""
				.formatted(cliente, mensaje, resumenPedido,
						(direccion != null && telefono != null)
								? "<p><b>Dirección:</b> " + direccion + "<br/><b>Teléfono:</b> " + telefono + "</p>"
								: "");
	}

	public String buildResumenPedidoHtml(String numeroPedido, List<String> productos, double total,
			String fechaRecogida) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div style='margin-bottom: 20px;'>");
		sb.append("<h3 style='color: #444;'>Pedido #").append(numeroPedido).append("</h3>");
		sb.append("<ul style='list-style: disc; padding-left: 20px;'>");
		for (String prod : productos) {
			sb.append("<li style='margin-bottom: 5px; color: #000000;'>").append(prod).append("</li>");
		}
		sb.append("</ul>");
		sb.append("</div>");
		sb.append("<p style='font-weight: bold; font-size: 16px;'>Total: ").append(String.format("%.2f", total))
				.append(" €</p>");
		if (fechaRecogida != null) {
			sb.append("<p><b>Recogida estimada:</b> ").append(fechaRecogida).append("</p>");
		}
		return sb.toString();
	}

}