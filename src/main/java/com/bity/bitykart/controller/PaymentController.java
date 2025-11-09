
package com.bity.bitykart.controller;

import com.bity.bitykart.dto.PaymentDto;
import com.bity.bitykart.model.PaymentRequest;
import com.bity.bitykart.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentDto paymentDto = paymentService.processPayment(paymentRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("payment", paymentDto);
            response.put("message", "Payment processed successfully");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(@PathVariable Long orderId) {
        try {
            PaymentDto paymentDto = paymentService.getPaymentByOrderId(orderId);
            return ResponseEntity.ok(paymentDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId) {
        try {
            PaymentDto paymentDto = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(paymentDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
// [file content end]