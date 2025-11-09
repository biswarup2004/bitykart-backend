
package com.bity.bitykart.repository;

import com.bity.bitykart.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findByPaymentStatus(String paymentStatus);
    List<Payment> findByPaymentMethod(String paymentMethod);
}
