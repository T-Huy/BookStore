package vn.java.EcommerceWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.java.EcommerceWeb.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
