package vn.java.EcommerceWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.java.EcommerceWeb.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
