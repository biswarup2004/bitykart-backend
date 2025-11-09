// [file name]: PaymentService.java
// [file content begin]
package com.bity.bitykart.service;

import com.bity.bitykart.dto.PaymentDto;
import com.bity.bitykart.model.Orders;
import com.bity.bitykart.model.Payment;
import com.bity.bitykart.model.PaymentRequest;
import com.bity.bitykart.repository.OrderRepository;
import com.bity.bitykart.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    public PaymentDto processPayment(PaymentRequest paymentRequest) {
        // Validate order exists
        Orders order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Validate amount matches order total
        if (Double.compare(order.getTotalAmount(), paymentRequest.getAmount()) != 0) {
            throw new RuntimeException("Payment amount does not match order total");
        }

        // Create payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentDate(new Date());

        // Set payment method specific details
        switch (paymentRequest.getPaymentMethod().toLowerCase()) {
            case "credit-card":
            case "debit-card":
                if (paymentRequest.getCardNumber() != null && paymentRequest.getCardNumber().length() >= 4) {
                    payment.setCardLastFour(paymentRequest.getCardNumber()
                            .substring(paymentRequest.getCardNumber().length() - 4));
                }
                break;
            case "upi":
                payment.setUpiId(paymentRequest.getUpiId());
                break;
            case "cod":
                // No additional details needed for COD
                break;
        }

        // SIMULATE PAYMENT - ALWAYS SUCCESS FOR DEMO
        boolean paymentSuccess = true; // Changed from random to always true

        if (paymentSuccess) {
            payment.setPaymentStatus("COMPLETED");

            // Update order status
            order.setStatus("CONFIRMED");
            orderRepository.save(order);

            System.out.println("Payment successful for order: " + order.getId());
        } else {
            payment.setPaymentStatus("FAILED");
            throw new RuntimeException("Payment processing failed");
        }

        Payment savedPayment = paymentRepository.save(payment);
        return convertToDto(savedPayment);
    }

    public PaymentDto getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order"));
        return convertToDto(payment);
    }

    public PaymentDto getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return convertToDto(payment);
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean simulatePaymentProcessing(PaymentRequest paymentRequest) {
        // For demo purposes, always return true to simulate successful payment
        return true;

        /* Commented out the random failure logic
        // Basic validation
        if ("credit-card".equals(paymentRequest.getPaymentMethod()) ||
                "debit-card".equals(paymentRequest.getPaymentMethod())) {
            // Validate card details
            if (paymentRequest.getCardNumber() == null ||
                    paymentRequest.getCardNumber().replace(" ", "").length() != 16) {
                return false;
            }
            if (paymentRequest.getCvv() == null || paymentRequest.getCvv().length() != 3) {
                return false;
            }
        } else if ("upi".equals(paymentRequest.getPaymentMethod())) {
            // Validate UPI ID
            if (paymentRequest.getUpiId() == null || !paymentRequest.getUpiId().contains("@")) {
                return false;
            }
        }

        // Simulate successful payment (90% success rate for demo)
        return Math.random() > 0.1;
        */
    }

    private PaymentDto convertToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setCardLastFour(payment.getCardLastFour());
        dto.setUpiId(payment.getUpiId());
        return dto;
    }
}
// [file content end]