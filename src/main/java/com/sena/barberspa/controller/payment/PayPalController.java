package com.sena.barberspa.controller.payment;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.sena.barberspa.model.enums.EstadoOrden;
import com.sena.barberspa.service.IOrdenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/paypal")
public class PayPalController {
    @Autowired
    private IOrdenService ordenService;

    // PayPal Configuration - Se recomienda mover esto a un archivo de configuración
    // (application.properties)
    private final String PAYPAL_CLIENT_ID = "Af2aprPDlFQMpXUr6Ak8e2bzlWjL_QctjCzKvuiyDkQYY3QTLwYXOgusOv63mxqr1vPQ8kh6cE0XTYOP";
    private final String PAYPAL_CLIENT_SECRET = "EG5icBw6Rw1eIpWsxjQOc17FwvqpXIt9Wqgr2nwHRAWG_KjxqweKxKt6Lr0cFpDPTUH9zyaOMerLwp7z";
    private final String PAYPAL_MODE = "sandbox";

    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<?> createPayPalOrder(@RequestBody Map<String, Object> payload, HttpSession session) {
        try {
            Double total = Double.parseDouble(payload.get("total").toString());
            APIContext apiContext = new APIContext(PAYPAL_CLIENT_ID, PAYPAL_CLIENT_SECRET, PAYPAL_MODE);

            Payment payment = new Payment();
            payment.setIntent("sale");

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");
            payment.setPayer(payer);

            RedirectUrls redirectUrls = new RedirectUrls();
            // URLs de redirección deben ser dinámicas o configurables
            redirectUrls.setCancelUrl("http://localhost:8080/paypal/cancel");
            redirectUrls.setReturnUrl("http://localhost:8080/paypal/success");
            payment.setRedirectUrls(redirectUrls);

            Amount amount = new Amount();
            amount.setCurrency("MXN");
            amount.setTotal(String.format("%.2f", total));

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription("Compra en BarberMusic&Spa");

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);
            payment.setTransactions(transactions);

            Payment createdPayment = payment.create(apiContext);

            Object idUsuarioObj = session.getAttribute("idUsuario");
            if (idUsuarioObj != null) {
                Long idUsuario = ((Number) idUsuarioObj).longValue();
                Object ordenIdObj = session.getAttribute("ordenId");
                if (ordenIdObj != null) {
                    Long ordenId = ((Number) ordenIdObj).longValue();
                    ordenService.findById(ordenId).ifPresent(orden -> {
orden.setEstado(EstadoOrden.EN_PROCESO);
                        ordenService.update(orden);
                    });
                }
            }

            String approvalUrl = null;
            for (Links link : createdPayment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    approvalUrl = link.getHref();
                    break;
                }
            }

            if (approvalUrl == null) {
                throw new PayPalRESTException("No se pudo obtener el enlace de aprobación de PayPal.");
            }

            Map<String, String> result = new HashMap<>();
            result.put("id", createdPayment.getId());
            result.put("approval_url", approvalUrl);

            return ResponseEntity.ok(result);

        } catch (PayPalRESTException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error con la API de PayPal: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/success")
    public String capturePayPalOrder(@RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId, HttpSession session) {
        try {

            APIContext apiContext = new APIContext(PAYPAL_CLIENT_ID, PAYPAL_CLIENT_SECRET, PAYPAL_MODE);
            Payment payment = Payment.get(apiContext, paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            Payment executedPayment = payment.execute(apiContext, paymentExecution);

            if (executedPayment.getState().equals("approved")) {
                Object ordenIdObj = session.getAttribute("ordenId");
                if (ordenIdObj != null) {
                    Long idOrden = ((Number) ordenIdObj).longValue();
                    ordenService.findById(idOrden).ifPresent(orden -> {
orden.setEstado(EstadoOrden.PAGADA);
                        ordenService.update(orden);
                    });
                }
                return "redirect:/pagos/pago_exitoso";
            } else {
                return "redirect:/pagos/pago_fallido";
            }

        } catch (PayPalRESTException e) {
            // Log a nivel de servidor
            System.err.println("Error al capturar el pago de PayPal: " + e.getMessage());
            return "redirect:/pagos/pago_fallido";
        } catch (Exception e) {
            System.err.println("Error inesperado durante la captura de PayPal: " + e.getMessage());
            return "redirect:/pagos/pago_fallido";
        }
    }

    @GetMapping("/cancel")
    public String cancelPayment() {
        return "redirect:/pagos/pago_fallido";
    }
}