package com.sena.barberspa.controller.payment;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.sena.barberspa.model.PaymentRequest;
import com.sena.barberspa.service.IOrdenService;
import com.sena.barberspa.service.MercadoPagoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mercadopago")
public class MercadoPagoController {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @Autowired
    private IOrdenService ordenService;

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processMercadoPagoPayment(@RequestBody PaymentRequest paymentRequest,
            HttpSession session) {
        try {
            // Se castea a Long en lugar de Integer para evitar ClassCastException si el ID
            // es grande
            Long ordenId = ((Number) session.getAttribute("ordenId")).longValue();
            if (ordenId == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "No se encontró una orden activa"));
            }

            Payment payment = mercadoPagoService.createPayment(paymentRequest);

            // Se convierte Long a Integer para la búsqueda, asumiendo que el ID de orden
            // cabe en un Integer
            ordenService.findById(ordenId).ifPresent(orden -> {
                orden.setEstado(payment.getStatus());
                ordenService.update(orden);
            });

            return ResponseEntity.ok(
                    Map.of(
                            "status", payment.getStatus(),
                            "status_detail", payment.getStatusDetail(),
                            "id", payment.getId().toString() // Convertir Long a String para consistencia
                    ));

        } catch (MPException | MPApiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar el pago: " + e.getMessage()));
        } catch (ClassCastException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "El ID de la orden en la sesión tiene un formato incorrecto."));
        }
    }
}